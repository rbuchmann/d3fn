(ns d3fn.scales
  (:require cljsjs.d3))

(defn ordinal []
  (-> js/d3
      .-scale
      .ordinal))

(defn range-points [domain range & [padding]]
  (-> (ordinal)
      (.domain (clj->js domain))
      (.rangePoints (clj->js range) (or padding 0))))

(defn range-bands [domain range & [padding outer-padding]]
  (-> (ordinal)
      (.domain (clj->js domain))
      (.rangeBands (clj->js range)
                   (or padding 0)
                   (or outer-padding 0))))
