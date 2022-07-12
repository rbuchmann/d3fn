(ns d3fn.animations)

(def update-frequency (/ 1000 120))

(defn schedule [state f maximal-time]
  (let [time-started (js/Date.)]
    (letfn [(state-fn []
              (let [elapsed (- (js/Date.) time-started)]
                (if (< elapsed maximal-time)
                  (do
                    (swap! state f (/ elapsed maximal-time))
                    (js/setTimeout state-fn update-frequency))
                  (swap! state f 1))))]
      (js/setTimeout state-fn update-frequency))))


(defn animate-property [k from to]
  (fn [state t]
    (assoc state k (+ from (* t (- to from))))))

(defn in-sequence [& animations]
  (let [total-duration (reduce + (map first animations))
        pieces (:pieces (reduce (fn [{:keys [time] :as state} [t f]]
                                  (let [new-time (+ (/ t total-duration) time)]
                                    (-> state
                                        (update :pieces conj [new-time (fn [state t]
                                                                         (f state
                                                                            (+ time
                                                                               (* (/ (- t time) (- new-time time))
                                                                                  (- new-time time)))))])
                                        (assoc :time new-time))))
                                {:pieces []
                                 :time 0}
                                animations))]
    pieces
    (fn [state t]
      (let [f (->> pieces
                   (drop-while (fn [[max-time _]] (> t max-time)))
                   first
                   second)]
        (f state t)))))

(def nothing identity)

(defn in-parallel [& animations]
  (fn [state t]
    (reduce (fn [current-state f] (f current-state t))
            state
            animations)))
