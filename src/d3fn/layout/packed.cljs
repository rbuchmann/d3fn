(ns d3fn.layout.packed
  (:require cljsjs.d3))

(defn to-hierarchy [root]
  (.hierarchy js/d3 (clj->js root)))

;; Siiiiiiiiigh...

(defn from-hierarchy [node]
  (js-delete node "parent")
  (update
   (into {}
         (for [k (.keys js/Object node)]
           [(keyword k) (js->clj (aget node k))]))
   :children #(map from-hierarchy %)))

(defn packed-bubbles [root]
  (let [summed (.sum (to-hierarchy root) (constantly 1))
        result ((.pack js/d3) summed)]
    (.log js/console result)
    (from-hierarchy result)))
