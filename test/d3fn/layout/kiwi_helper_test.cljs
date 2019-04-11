(ns d3fn.layout.kiwi-helper-test
  (:require [d3fn.layout.kiwi-helper :as k]
            [cljs.test               :as t :include-macros true :refer [is]]
            [devcards.core           :as dc :refer-macros [deftest defcard-rg]]))

(defn max-test []
  (let [s       (k/make-solver)
        [a b c] (repeatedly 3 k/make-variable)
        max     (k/max s a b c)]
    (-> s
        (k/add-edit-variables [a b c] :strong)
        (k/suggest-value a 20)
        (k/suggest-value b 30)
        (k/suggest-value c 70)
        k/solve)
    @max))

(defn center-test []
  (let [s (k/make-solver)
        [x y] [(k/make-expression 50) (k/make-expression 60)]
        [w h] [(k/make-expression 10) (k/make-expression 10)]
        [cx cy] (k/center s [x y] [w h])]
    (k/solve s)
    [@cx @cy]))

(defcard-rg basics-test-card
  [:div
   [:h1 "Max test"]
   [:p (max-test)]
   [:h1 "Center test"]
   [:p (str (center-test))]])
