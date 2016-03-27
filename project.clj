(defproject soy-clj "0.2.1"
  :description "An idiomatic Clojure wrapper for Google's Closure templating system."
  :url "https://github.com/codahale/soy-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/core.cache "0.6.4"]
                 [com.google.template/soy "2016-01-12"
                  :exclusions [args4j]]]
  :plugins [[codox "0.9.4"]]
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
