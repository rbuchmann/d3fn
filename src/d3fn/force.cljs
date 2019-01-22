(ns d3fn.force
  (:require cljsjs.d3))

(defn track-force-layout [state-atom {:keys [size nodes links] :as opts}]
  (let [indexed-nodes (map-indexed (fn [i node] (assoc node :index i)) nodes)
        link-force (js/d3.forceLink. (clj->js links))
        _ (.distance link-force 50)
        force (-> js/d3
                  (.forceSimulation (clj->js indexed-nodes))
                  (.force "link" link-force)) ]
    (.on force "tick" (fn [_]
                        (swap! state-atom assoc
                               :nodes (js->clj (.nodes force)
                                               :keywordize-keys true)
                               :links (js->clj (.links link-force)
                                               :keywordize-keys true))))
    state-atom))
