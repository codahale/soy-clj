(ns soy-clj.core
  "An idiomatic Clojure wrapper for Google Closure templates."
  (:require [clojure.core.cache :as cache]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as string]
            [clojure.walk :as walk])
  (:import (com.google.template.soy SoyFileSet)
           (com.google.template.soy.data SanitizedContent$ContentKind
                                         UnsafeSanitizedContentOrdainer)
           (com.google.template.soy.shared SoyGeneralOptions)))

(def ^:private template-cache
  "Default to keeping the 32 most-used templates in cache."
  (atom (cache/lu-cache-factory {})))

(defn set-cache
  "Sets the cache for parsed templates. If set to `nil`, templates will be
  parsed on each invocation of `build`."
  [cache]
  (reset! template-cache cache))

(defn- parse-uncached
  [files]
  (let [builder (reduce #(.add %1 (io/resource %2)) (SoyFileSet/builder) files)
        opts (SoyGeneralOptions.)]
    (.setStrictAutoescapingRequired opts true)
    (.setGeneralOptions builder opts)
    (.. builder (build) (compileToTofu))))

(defn parse
  "Given the filename (or a vector of filenames) of a Closure template on the
  classpath, parses the templates and returns a compiled set of templates."
  [file-or-files]
  (let [files (flatten (vector file-or-files))]
    (swap! template-cache #(cache/through parse-uncached % files))
    (get @template-cache files)))

(defn- dash-split
  [s]
  (string/split (name s) #"-"))

(defn- camel-case
  [s]
  (let [uc (->> s (dash-split) (map string/capitalize) (string/join) (vec))]
    (string/join (cons (string/lower-case (first uc)) (next uc)))))

(defn- camelize-keys
  [m]
  (let [f (fn [[k v]] (if (keyword? k) [(camel-case k) v] [k v]))]
    ;; only apply to maps
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(def ^:private content-kind
  {SanitizedContent$ContentKind/ATTRIBUTES :attributes
   SanitizedContent$ContentKind/CSS :css
   SanitizedContent$ContentKind/HTML :html
   SanitizedContent$ContentKind/JS :js
   SanitizedContent$ContentKind/TEXT :text
   SanitizedContent$ContentKind/URI :uri})

(def ^:private content-kind-enum (set/map-invert content-kind))

(defn render
  "Given a compiled set of templates, renders the named template with the given
  data and returns the result as a string as well as the _kind_ of data in the
  template (e.g. `:html`). Data keys of the form `:one-two` are converted into
  template variables of the form `oneTwo`."
  [templates template-name data]
  (let [content (.. templates
                    (newRenderer template-name)
                    (setData (camelize-keys data))
                    (renderStrict))]
    [(.getContent content)
     (content-kind (.getContentKind content))]))

(defn ordain-as-safe
  "Ordains the given content as safe content of the given kind which will not be
  escaped inside that kind's context. Use this sparingly, as it entirely
  bypasses Soy's XSS protection."
  [content kind]
  (UnsafeSanitizedContentOrdainer/ordainAsSafe content
                                               (content-kind-enum kind)))
