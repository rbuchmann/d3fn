(ns d3fn.layout.constraint-test
  (:require [d3fn.layout.constraint :as c]
            [cljs.test              :as t :include-macros true :refer [is]]
            [devcards.core          :as dc :refer-macros [deftest defcard-rg]]))

(defn max-test []
  (let [s       (c/make-solver)
        [a b c] (repeatedly 3 c/make-variable)
        max     (c/max s a b c)]
    (-> s
        (c/add-edit-variables [a b c] :strong)
        (c/suggest-value a 20)
        (c/suggest-value b 30)
        (c/suggest-value c 70)
        c/solve)
    @max))

(defn center-test []
  (let [s (c/make-solver)
        [x y] [(c/make-expression 50) (c/make-expression 60)]
        [w h] [(c/make-expression 10) (c/make-expression 10)]
        [cx cy] (c/center s [x y] [w h])]
    (c/solve s)
    [@cx @cy]))


(defcard-rg basics-test-card
  [:div
   [:h1 "Max test"]
   [:p (max-test)]
   [:h1 "Center test"]
   [:p (str (center-test))]])
