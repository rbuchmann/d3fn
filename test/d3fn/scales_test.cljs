(ns d3fn.scales-test
  (:require [d3fn.scales   :as scales]
            [cljs.test     :as test :refer-macros [is]]
            [devcards.core :as dc :refer-macros [defcard deftest defcard-rg]]))

(defn within-epsilon [x target e]
  (< (- target e) x (+ target e)))

(deftest ordinal-range-points
  (let [s (scales/range-points (range 10) [0 20] 1)]
    (is (within-epsilon (s 0) 1 0.0001))
    (is (within-epsilon (s 1) 3 0.0001))))

(deftest ordinal-range-points-no-padding
  (let [s (scales/range-points (range 11) [0 20])]
    (is (within-epsilon (s 0) 0 0.0001))
    (is (within-epsilon (s 1) 2 0.0001))))
