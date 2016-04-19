(ns d3fn.animations
  (:require [reagent.core :as reagent]
            [cljs.core.async :as async :refer [<! >!]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defn ramp-channel [& {:keys [xf samples time]}]
  (let [n (or samples 60)
        t (or time 1000)
        interval (/ t n)
        increment (/ 1 n)
        out (if xf
              (async/chan 1 xf)
              (async/chan))]
    (go-loop [i 0]
      (when (and (< (- i increment) 1) (>! out (min i 1)))
        (<! (async/timeout interval))
        (recur (+ i increment))))
    out))


(defn interpolate [a b t]
  (+ (* t b) (* a (- 1 t))))

(defn lerp [state key to]
  (let [from (get @state key)
        ch (ramp-channel)]
    (go-loop []
      (when-let [val (<! ch)]
        (swap! state assoc key (interpolate from to val))
        (recur)))))
