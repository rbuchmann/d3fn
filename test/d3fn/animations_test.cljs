(ns d3fn.animations-test
  (:require [cljs.test :as test :refer-macros [is]]
            [devcards.core :as dc :refer-macros [defcard deftest defcard-rg]]
            [d3fn.animations :as animations]
            [reagent.core :as r]
            [cljs.core.async :as async :refer [<! >!]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defn display []
  (let [state (r/atom 0)
        c (animations/ramp-channel)]
    (go-loop []
      (let [val (<! c)]
        (reset! state val)
        (recur)))
    (fn []
      [:div [:p @state]])))

(defcard-rg foo
  [display])

(defn ball []
  (let [state (r/atom {:x 5})
        anim (animations/lerp state :x 200)]
    (fn []
      [:svg {:width 300
             :height 20}
       [:circle {:cx (:x @state)
                 :cy 10
                 :r 10}]])))

(defcard-rg move
  [ball])
