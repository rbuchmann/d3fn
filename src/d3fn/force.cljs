(ns d3fn.force
  (:require [reagent.core :as reagent]
            [d3fn.render :as render]
            [cljsjs.d3]))

(defn graph [[width height] state]
  (let [{:keys [nodes links]} @state]
    [:svg {:width width
           :height height}
     (map-indexed (fn [i node]
                    ^{:key i} [render/node node])
                  nodes)
     (map-indexed (fn [i link]
                    ^{:key i} [render/link link])
                  links)]))

(defn track-force-layout [size nodes links]
  (let [state-atom (reagent/atom {})
        js-nodes (clj->js nodes)
        js-links (clj->js links)
        force (-> js/d3
                  .-layout
                  .force
                  (.nodes js-nodes)
                  (.links js-links)
                  (.size (clj->js size)))]
    (.on force "tick" (fn [evt]
                        (swap! state-atom assoc
                               :nodes (js->clj js-nodes :keywordize-keys true)
                               :links (js->clj js-links :keywordize-keys true))))
    (.start force)
    state-atom))

(defn dynamic-graph [size nodes links]
  (let [state (track-force-layout size nodes links)]
    [graph size state]))
