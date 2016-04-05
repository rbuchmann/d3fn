(ns d3fn.helpers
  (:require [camel-snake-kebab.core :refer [->camelCaseSymbol]]))

(defmacro mapping-map [& keys]
  (into {}
        (for [k keys]
          [k `(fn [obj# arg#] (. obj# (~(->camelCaseSymbol k) (~'clj->js arg#))))])))
