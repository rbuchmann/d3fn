(ns d3fn.core
  (:require [reagent.dom     :as rd]
            [d3fn.box-layout :as bl]
            [d3fn.animations :as a]
            [reagent.core    :as r]))

(defn test-layout []
  (let [heights     [1 5 2 6 7 10 20]
        n           (count heights)
        widths      (repeat n 5)
        constraints [(bl/zip-value :rect/bars :h heights)
                     (bl/zip-value :rect/bars :w widths)
                     (bl/align-bottom (bl/all-box-paths :rect/bars n))
                     (bl/horizontal-gap 2 (bl/all-box-paths :rect/bars n))
                     (bl/horizontal-gap 1 :hline/y-axis [:rect/bars 0])
                     (bl/fix-value [:vline/y-axis :h] 20)
                     (bl/fix-value [:hline/x-axis :y] 20)
                     (bl/fix-value [:hline/x-axis :w] 100)
                     (bl/vertical-gap 1 [:rect/bars 0] :hline/x-axis)]
        state       (bl/layout! constraints)]
    [:svg {:view-box "-1 0 60 60"}
     (into [:<>]
           (bl/render state))]))



(defn img-stack [x offset]
  [:g
   [:image {:x x :y (+ -200 (mod offset 600))         :width 200 :height 200 :href "/img/exa_bw.svg"}]
   [:image {:x x :y (+ -200 (mod (+ 200 offset) 600)) :width 200 :height 200 :href "/img/kiwi.svg"}]
   [:image {:x x :y (+ -200 (mod (+ 400 offset) 600)) :width 200 :height 200 :href "/img/d3fn.svg"}]])

(defn robot-test []
  (let [state     (r/atom {:offset-left  0
                           :offset-mid   200
                           :offset-right 400})
        animation (a/in-parallel
                   (a/in-sequence [6 (a/animate-property :offset-left 0 2400)]
                                  [2 a/nothing])
                   (a/in-sequence [1 a/nothing]
                                  [6 (a/animate-property :offset-mid 200 2600)]
                                  [1 a/nothing])
                   (a/in-sequence [2 a/nothing]
                                  [6 (a/animate-property :offset-right 400 2800)]))]
    (a/schedule state animation 4000)
    (fn []
      [:svg {:view-box "0 0 600 200"}
       [img-stack 0   (:offset-left  @state)]
       [img-stack 200 (:offset-mid   @state)]
       [img-stack 400 (:offset-right @state)]
       ])))

(defn init []
  (rd/render #_[test-layout] [robot-test]
             (.getElementById js/document "root")))


(init)
