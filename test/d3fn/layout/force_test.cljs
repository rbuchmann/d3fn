(ns d3fn.layout.force-test
  (:require [d3fn.layout.force :as force]
            [reagent.core      :as r]
            [cljs.test         :as test :refer-macros [is]]
            [devcards.core     :as dc :refer-macros [defcard deftest defcard-rg]]))

(defn render-graph [state]
  (let [{:keys [nodes links]} @state]
    [:svg {:width 640
           :height 480}
     (into [:g]
           (for [{:keys [source target]} links]
             (let [{x1 :x y1 :y} source
                   {x2 :x y2 :y} target]
               [:line {:x1 x1 :y1 y1 :x2 x2 :y2 y2
                       :stroke :black}])))
     (into [:g]
           (for [{:keys [x y r]} nodes]
             [:circle {:cx x :cy y :r r}]))]))

(defcard-rg graph
  [render-graph
   (force/track-force-layout
    (r/atom {})
    {:nodes [{:x 100 :y 200 :r 5} {:x 300 :y 200 :r 10}]
     :links [{:source 0
              :target 1}]})])
