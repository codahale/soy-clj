(ns soy-clj.bench-test
  (:require [clojure.core.cache :as cache]
            [clojure.test :refer :all]
            [criterium.core :as c]
            [soy-clj.core :as soy]))

(defn- sep
  [s]
  (printf "\n\n######  %s  ######\n" s))

(deftest ^:bench parse-uncached-bench
  (sep "Parsing (uncached)")
  (soy/set-cache (cache/ttl-cache-factory {} :ttl 0))
  (c/bench (soy/parse "example.soy")))

(deftest ^:bench parse-cached-bench
  (sep "Parsing (cached)")
  (soy/set-cache (cache/lu-cache-factory {}))
  (soy/parse "example.soy")
  (c/bench (soy/parse "example.soy")))

(deftest ^:bench render-big-bench
  (sep "Rendering (big list)")
  (let [templates (soy/parse "example.soy")
        data      {:items (vec (map str (range 1 1000)))}]
    (c/bench (soy/render templates "examples.simple.list" data))))

(deftest ^:bench render-small-bench
  (sep "Rendering (small list)")
  (let [templates (soy/parse "example.soy")
        data      {:items (vec (map str (range 1 50)))}]
    (c/bench (soy/render templates "examples.simple.list" data))))

(deftest ^:bench render-simple-bench
  (sep "Rendering (simple)")
  (let [templates (soy/parse "example.soy")
        data      {:bar "bar"}]
    (c/bench (soy/render templates "examples.simple.basic" data))))
