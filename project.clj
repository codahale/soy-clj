(defproject soy-clj "0.1.7-SNAPSHOT"
  :description "An idiomatic Clojure wrapper for Google's Closure templating system."
  :url "https://github.com/codahale/soy-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/core.cache "0.6.4"]
                 [com.google.template/soy "2015-04-10"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.8.0-RC2"]
                                  [criterium "0.4.3"]]}}
  :plugins [[codox "0.9.0"]]
  :test-selectors {:default #(not-any? % [:bench])
                   :bench   :bench}
  :aliases {"bench" ["test" ":bench"]}
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]
  :global-vars {*warn-on-reflection* true})
