(ns d3fn.helpers
  (:require-macros [d3fn.helpers :as helpers :refer [mapping-map]]))

(defn configure-with [obj fn-map m]
  (reduce (fn [to-configure [k v]]
            (let [f (k fn-map)]
              (f to-configure v)))
          obj
          m)
  obj)
