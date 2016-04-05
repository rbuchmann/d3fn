(ns d3fn.helpers
  (:require [camel-snake-kebab.core :refer [->camelCaseSymbol]]))

(defmacro mapping-map [& keys]
  (into {}
        (for [k keys]
          [k `(memfn ~(->camelCaseSymbol k))])))
