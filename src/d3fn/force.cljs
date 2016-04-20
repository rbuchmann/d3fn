(ns d3fn.force
  (:require [reagent.core :as reagent]
            [d3fn.render :as render]
            [d3fn.helpers :as helpers]
            [cljsjs.d3]))

(defn graph [[width height] state]
  (let [{:keys [nodes links]} @state]
    [:svg {:width width
           :height height}
     (map-indexed (fn [i node]
                    ^{:key i} [render/render node])
                  nodes)
     (map-indexed (fn [i link]
                    ^{:key i} [render/render link])
                  links)]))

(def force-map (helpers/mapping-map :nodes :links :size))

(defn track-force-layout [size nodes links & [opts]]
  (let [state-atom (reagent/atom {})
        force (-> js/d3
                  .-layout
                  .force
                  (helpers/configure-with
                   force-map
                   (merge
                    {:nodes nodes
                     :links links
                     :size size}
                    opts)))]
    (.on force "tick" (fn [evt]
                        (swap! state-atom assoc
                               :nodes (map #(merge % {:type :node}) (js->clj (.nodes force) :keywordize-keys true))
                               :links (map #(merge % {:type :link}) (js->clj (.links force) :keywordize-keys true)))))
    (.start force)
    state-atom))

(defn dynamic-graph [size nodes links]
  (let [state (track-force-layout size nodes links)]
    [graph size state]))
