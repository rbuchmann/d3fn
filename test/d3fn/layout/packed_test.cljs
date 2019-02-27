(ns d3fn.layout.packed-test
  (:require [d3fn.layout.packed :as sut]
            [reagent.core       :as r]
            [cljs.test          :as test :refer-macros [is]]
            [devcards.core      :as dc :refer-macros [deftest defcard-rg]]))

(defn render-bubbles [{:keys [x y r children]}]
  (into [:g [:circle {:cx x
                      :cy y
                      :r r
                      :fill :blue
                      :stroke :black
                      :stroke-width 0.01}]]
        (map render-bubbles children)))

(defcard-rg bubble-pack
  (let [bubbles (sut/packed-bubbles {:name     "foo"
                                 :children [{:name "bar"}
                                            {:name "baz"}]})]
    [:svg {:width 640 :height 480 :view-box [0 0 1 1]}
     (render-bubbles bubbles)]))
