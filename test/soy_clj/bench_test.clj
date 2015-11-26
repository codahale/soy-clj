(ns soy-clj.bench-test
  (:require [clojure.core.cache :as cache]
            [clojure.test :refer :all]
            [criterium.core :as c]
            [soy-clj.core :as soy]))

(deftest ^:bench parse-uncached-bench
  (println "\n\n=== Parsing (uncached)")
  (soy/set-cache (cache/ttl-cache-factory {} :ttl 0))
  (c/bench (soy/parse "example.soy")))

(deftest ^:bench parse-cached-bench
  (println "\n\n=== Parsing (cached)")
  (soy/set-cache (cache/lu-cache-factory {}))
  (soy/parse "example.soy")
  (c/bench (soy/parse "example.soy")))

(deftest ^:bench render-big-bench
  (println "\n\n=== Rendering (big list)")
  (soy/set-cache (cache/lu-cache-factory {}))
  (let [templates (soy/parse "example.soy")
        data {:items (vec (map str (range 1 1000)))}]
    (c/bench (soy/render templates "examples.simple.list" data))))

(deftest ^:bench render-small-bench
  (println "\n\n=== Rendering (small list)")
  (soy/set-cache (cache/lu-cache-factory {}))
  (let [templates (soy/parse "example.soy")
        data {:items (vec (map str (range 1 50)))}]
    (c/bench (soy/render templates "examples.simple.list" data))))

(deftest ^:bench render-simple-bench
  (println "\n\n=== Rendering (simple)")
  (soy/set-cache (cache/lu-cache-factory {}))
  (let [templates (soy/parse "example.soy")
        data {:bar "bar"}]
    (c/bench (soy/render templates "examples.simple.basic" data))))
