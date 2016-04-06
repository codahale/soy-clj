(ns soy-clj.core
  "An idiomatic Clojure wrapper for Google Closure templates."
  (:require [clojure.core.cache :as cache]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as string]
            [clojure.walk :as walk])
  (:import (java.net URL)
           (com.google.template.soy SoyFileSet SoyFileSet$Builder)
           (com.google.template.soy.data SanitizedContent$ContentKind
                                         UnsafeSanitizedContentOrdainer)
           (com.google.template.soy.jssrc SoyJsSrcOptions)
           (com.google.template.soy.jssrc.restricted JsExpr JsExprUtils)
           (com.google.template.soy.shared SoyGeneralOptions)
           (com.google.template.soy.shared.restricted Sanitizers
                                                      TagWhitelist$OptionalSafeTag)
           (com.google.template.soy.tofu SoyTofu)))

(def ^:private cache
  "Default to keeping the 32 most-used templates or JS in cache."
  (atom (cache/lu-cache-factory {})))

(defn set-cache
  "Sets the cache for parsed templates and compiled JS."
  [cache-impl]
  (reset! cache cache-impl))

(def ^:private opts
  "Default to requiring autoescaped templates."
  (doto (SoyGeneralOptions.)
    (.setStrictAutoescapingRequired true)))

(def ^:private js-opts
  "Use only the defaults for JS compilation."
  (SoyJsSrcOptions.))

(def ^:dynamic *builder-fn*
  "The function used to create a SoyFileSet$Builder."
  #(SoyFileSet/builder))

(def ^:dynamic *preprocessor-fn*
  "The function used to pre-process the template contents."
  identity)

(defn- add-file
  [^SoyFileSet$Builder builder file]
  (if-let [res (if (instance? URL file) file (io/resource file))]
    (.add builder ^String (*preprocessor-fn* (slurp res)) (str file))
    (throw (IllegalArgumentException. (str "Unable to open " file)))))

(defn- ^SoyFileSet build
  "Builds a fileset from the given files."
  [files]
  (let [^SoyFileSet$Builder builder (*builder-fn*)]
    (run! (partial add-file builder) files)
    (.setGeneralOptions builder opts)
    (.build builder)))

(def ^:private prelude
  "A bit of required JS."
  "if(typeof goog == 'undefined') {var goog = {};}")

(defn compile-to-js
  "Compile the given set of templates to Javascript."
  [file-or-files]
  (let [files (flatten (vector file-or-files))]
    (if-let [found (cache/lookup @cache [:js files])]
      found
      (let [js (->> (.compileToJsSrc (build files) js-opts nil)
                    (cons prelude)
                    (string/join "\n"))]
        (swap! cache assoc [:js files] js)
        js))))

(defn- parse-uncached
  "Returns a compiled set of templates from the given files."
  [files]
  (.. (build files) (compileToTofu)))

(defn parse
  "Given the filename (or a sequence of filenames) of a Closure template on the
  classpath, parses the templates and returns a compiled set of templates."
  [file-or-files]
  (let [files (vec (flatten (vector file-or-files)))]
    (if-let [found (cache/lookup @cache [:tofu files])]
      found
      (let [templates (parse-uncached files)]
        (swap! cache assoc [:tofu files] templates)
        templates))))

(defn- camel-case
  "Converts a symbol like `:blah-blah` into a string like `blahBlah`."
  [s]
  (let [ss (string/split (name s) #"-")]
    (string/join (cons (string/lower-case (first ss))
                       (map string/capitalize (next ss))))))

(defn- camelize-keys
  "Recursively transforms a map, converting all keyword keys into camel-case
  strings."
  ^java.util.Map [m]
  (let [f (fn [[k v]] (if (keyword? k) [(camel-case k) v] [k v]))]
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn content-type
  "The MIME content-type of the given kind."
  [kind]
  (case kind
    :css "text/css; charset=utf-8"
    :html "text/html; charset=utf-8"
    :js "text/javascript; charset=utf-8"
    "text/plain; charset=utf-8"))

(def ^:private content-kind
  "A map of ContentKind enums to happy little symbols."
  {SanitizedContent$ContentKind/ATTRIBUTES :attributes
   SanitizedContent$ContentKind/CSS :css
   SanitizedContent$ContentKind/HTML :html
   SanitizedContent$ContentKind/JS :js
   SanitizedContent$ContentKind/TEXT :text
   SanitizedContent$ContentKind/TRUSTED_RESOURCE_URI :trusted-resource-uri
   SanitizedContent$ContentKind/URI :uri})

(def ^:private content-kind-enum
  "A map of happy little symbols to ContentKind enums."
  (set/map-invert content-kind))

(defn render
  "Given a parsed set of templates, renders the named template with the given
  data and returns the result as a string as well as the _kind_ of data in the
  template (e.g. `:html`). Data keys of the form `:one-two` are converted into
  template variables of the form `oneTwo`. Optionally, an expected kind may be
  passed. If none is given, `:html` is assumed."
  [^SoyTofu templates ^String template-name data & [kind]]
  (let [content (.. templates
                    (newRenderer template-name)
                    (setContentKind (content-kind-enum (or kind :html)))
                    (setData (camelize-keys data))
                    (renderStrict))]
    [(.getContent content) (content-kind (.getContentKind content))]))

(defn- safe-str
  [content kind]
  (UnsafeSanitizedContentOrdainer/ordainAsSafe content (content-kind-enum kind)))

(defn- safe-jsexpr
  [content kind]
  (JsExprUtils/maybeWrapAsSanitizedContent (content-kind-enum kind) content))

(defn ordain-as-safe
  "Ordains the given content as safe content of the given kind which will not be
  escaped inside that kind's context. Use this sparingly, as it entirely
  bypasses Soy's XSS protection."
  [content kind]
  (cond
    (string? content)          (safe-str content kind)
    (instance? JsExpr content) (safe-jsexpr content kind)
    :else                      (throw (ex-info "Unrecognized content type"
                                               {:content content :kind kind}))))

(defn clean-html
  "Parses the given string as HTML and removes all tags except for basic
  formatting tags: `<b>`, `<br>`, `<em>`, `<i>`, `<s>`, `<sub>`, `<sup>`,
  `<u>`. Additionally, the use of `<ul>`, `<ol>`, `<li>`, and `<span>` can be
  enabled by passing the symbols `:ul` etc. as additional arguments."
  [^String s & safe-tags]
  (Sanitizers/cleanHtml s ^java.util.Collection
                        (vec (map #(TagWhitelist$OptionalSafeTag/fromTagName
                                    (name %)) safe-tags))))
