(ns d3fn.force-test
  (:require [d3fn.force :as force]
            [cljs.test :as test :refer-macros [is]]
            [devcards.core :as dc :refer-macros [defcard deftest defcard-rg]]))

(defcard-rg graph
  [force/graph [400 400]
   (atom {:nodes [{:x 100 :y 200 :r 5} {:x 300 :y 200 :r 10}]
          :links [{:source {:x 100 :y 200 :r 5}
                   :target {:x 300 :y 200 :r 10}}]})])

(defn dynamic-graph-test []
  [force/dynamic-graph [400 400]
   [{:text "foo" :x 100} {:text "bar" :x 300}]
   [{:source 0
     :target 1}]])

(defcard-rg dynamic-graph
  [dynamic-graph-test])
