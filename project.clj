(defproject com.codahale/soy-clj "0.3.3-SNAPSHOT"
  :description "An idiomatic Clojure wrapper for Google's Closure templating system."
  :url "https://github.com/codahale/soy-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.codahale/guava-cache-clj "0.1.2"]
                 [com.google.template/soy "2017-02-01"
                  :exclusions [args4j
                               com.google.gwt/gwt-user
                               com.google.guava/guava-testlib
                               org.json/json
                               com.google.code.gson/gson]]]
  :plugins []
  :test-selectors {:default #(not-any? % [:bench])
                   :bench   :bench}
  :aliases {"bench" ["test" ":bench"]}
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]
  :global-vars {*warn-on-reflection* true}
  :profiles {:dev [:project/dev :profiles/dev]
             :test [:project/test :profiles/test]
             :profiles/dev {:dependencies [[org.clojure/clojure "1.8.0"]
                                           [criterium "0.4.4"]]}
             :profiles/test {}
             :project/dev {:source-paths ["dev"]
                           :repl-options {:init-ns user}}
             :project/test {:dependencies []}})
