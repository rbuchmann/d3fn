(ns d3fn.core
  (:require [reagent.dom     :as rd]
            [d3fn.box-layout :as bl]))

(defn test-layout []
  (let [state       (atom {})
        heights     [1 5 2 6 7 10 20]
        widths      (repeat (count heights) 5)
        constraints [(bl/zip-value state :rect/bars :h heights)
                     (bl/zip-value state :rect/bars :w widths)
                     (bl/align-bottom state (bl/get-box-keys state :rect/bars))
                     (bl/fixed-gap state (bl/get-box-keys state :rect/bars) 2)
                     (bl/above state :rect/bars :hline/x-axis)
                     (bl/align-center-x state :rect/bars :vline/x-ticks)
                     (bl/above state :rect/bars :vline/x-ticks)]]
    (bl/layout! constraints)
    [:svg {:view-box "0 0 60 60"}
     (into [:g]
           (bl/render state))]))

(defn init []
  (rd/render [test-layout]
             (.getElementById js/document "root")))


(init)
