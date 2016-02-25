(ns soy-clj.core-test
  (:require [clojure.core.cache :as cache]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.test :refer :all]
            [soy-clj.core :refer :all :as soy-clj])
  (:import [com.google.template.soy SoyFileSet SoyFileSet$Builder]))

(deftest content-type-test
  (testing "The content types of various kinds"
    (is (= "text/css; charset=utf-8" (content-type :css)))
    (is (= "text/html; charset=utf-8" (content-type :html)))
    (is (= "text/javascript; charset=utf-8" (content-type :js)))
    (is (= "text/plain; charset=utf-8" (content-type :some-other-kind)))))

(deftest clean-html-test
  (testing "Cleaning an HTML string"
    (is (= (ordain-as-safe "<em>woo</em> woo();" :html)
           (clean-html "<em>woo</em> <li>woo();</li>"))))
  (testing "Cleaning an HTML string with optional safe tags"
    (is (= (ordain-as-safe "<em>woo</em> <span>woo();</span>" :html)
           (clean-html "<em>woo</em> <span>woo();</span>" :span)))))

(deftest compile-to-js-test
  (testing "Compiling a missing template to Javascript"
    (is (thrown? IllegalArgumentException
                 (compile-to-js "bad.soy"))))
  (testing "Compiling templates to Javascript"
    (is (= (slurp "test/example.js")
           (compile-to-js "example.soy")))))

(deftest parse-test
  (testing "Parsing a template without a cache"
    (set-cache (cache/lu-cache-factory {}))
    (is (parse "example.soy"))
    (is (parse ["example.soy"]))
    (is (not (cache/has? @@#'soy-clj/template-cache "example.soy")))
    (is (cache/has? @@#'soy-clj/template-cache ["example.soy"])))
  (testing "Parsing a template without a cache"
    (is (parse "example.soy"))
    (is (parse ["example.soy"])))
  (testing "Overriding the builder"
    (binding [*builder-fn* (fn [] (let [builder (SoyFileSet/builder)]
                                    (.add builder (io/file "test/example.soy"))
                                    builder))]
      (is (= ["Hello, Mr. World!" :text]
             (render (parse "other-example.soy") "examples.simple.exampleText"
                     {:name "Mr. World"}
                     :text)))))
  (testing "Preprocessing a template"
    (set-cache (cache/lu-cache-factory {}))
    (binding [*preprocessor-fn* #(str %
                                      "\n\n"
                                      (->> (slurp "test/other-example.soy")
                                           (string/split-lines)
                                           (drop 1)
                                           (string/join "\n")))]
      (is (= ["Yes, boss." :html]
             (render (parse "example.soy") "examples.simple.addition"
                     {:name "boss"}))))))

(deftest render-test
  (testing "Rendering a template"
    (is (= [(str "<p><a href=\"/welcome?name=Mr.%20World\">Welcome</a>"
                 "Bonjour Mr. World!</p>")
            :html]
           (render (parse "example.soy") "examples.simple.helloName"
                   {:name "Mr. World"
                    :greeting-word "Bonjour"}))))
  (testing "Rendering a text template"
    (is (= ["Hello, Mr. World!"
            :text]
           (render (parse "example.soy") "examples.simple.exampleText"
                   {:name "Mr. World"}
                   :text))))
  (testing "Contextually auto-escaping"
    (is (= [(str "<a href=\"#\" onclick=\"setName('\\x27, "
                 "alert(\\x27XSS\\x27), \\x27')\">&#39;, "
                 "alert(&#39;XSS&#39;), &#39;</a>")
            :html]
           (render (parse "example.soy") "examples.simple.example"
                   {:name "', alert('XSS'), '"}))))
  (testing "Passing complex structures to nested templates"
    (is (= [(str "<p><a href=\"/welcome?name=Alice\">Welcome</a>"
                 "Hello Alice!</p><br><p><a href=\"/welcome?name=Bob\">"
                 "Welcome</a>Hello Bob!</p><br><p>"
                 "<a href=\"/welcome?name=Carol\">Welcome</a>"
                 "Hello Carol!</p><br><p><a href=\"/welcome?name=Dave\">"
                 "Welcome</a>Hello Dave!</p>")
            :html]
           (render (parse "example.soy") "examples.simple.helloNames"
                   {"name" "Alice"
                    :additional-names ["Bob" "Carol" "Dave"]}))))
  (testing "Passing pre-sanitized HTML"
    (is (= [(str "<p><a href=\"/welcome?name=Mr.%20%3Ci%3EWorld%3Ci%3E\">"
                 "Welcome</a>Bonjour Mr. <i>World<i>!</p>")
            :html]
           (render (parse "example.soy") "examples.simple.helloName"
                   {:name (ordain-as-safe "Mr. <i>World<i>" :html)
                    :greeting-word "Bonjour"})))))
