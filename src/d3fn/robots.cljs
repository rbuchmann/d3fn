(ns d3fn.robots
  (:require [clojure.spec.alpha     :as s]
            [devcards.core          :as dc :refer-macros [defcard-rg]]
            [reagent.core           :as r]
            [clojure.test.check.generators]
            [clojure.spec.gen.alpha :refer [generate]]
            [clojure.pprint         :refer [pprint]]))

;; Bot Spec

(s/def ::angle (s/and number?
                      #(>= % 0)
                      #(<= % 45)))

(s/def ::hands #{:claw :lazor})
(s/def ::arms (s/keys :req [::hands ::angle]))

(s/def ::legs #{:rolls :legs})

(s/def ::shape #{:round :square})
(s/def ::antenna #{:radio})
(s/def ::head (s/keys :req [::shape ::antenna]))

(s/def ::torso #{:default})

(s/def ::color #{:red :blue :green})

(s/def ::robot (s/keys :req [::legs ::torso ::arms ::head ::color]))








;; Rendering

(defn render-legs [legs]
  (case legs
    :rolls
    [:g
     [:circle {:cx 200 :cy 410 :r 10}]
     [:circle {:cx 240 :cy 410 :r 10}]
     [:circle {:cx 280 :cy 410 :r 10}]]
    :legs
    [:g
     [:rect {:x 210 :y 400 :width :20 :height 50}]
     [:rect {:x 250 :y 400 :width :20 :height 50}]]))

(defn render-torso [torso]
  [:rect {:x 200 :y 300 :width 80 :height 100}])

(defn render-antenna [antenna offset]
  (case antenna
    :radio
    [:g
     [:rect {:x 238 :y (+ 240 offset) :width 4 :height 20}]]))

(defn render-head [{:keys [::shape ::antenna]}]
  [:g
   (case shape
     :square
     [:rect {:x 220 :y 260 :width 40 :height 40}]
     :round
     [::circle {:cx 240 :cy 270 :r 30}])
   [:circle {:cx 230 :cy 280 :r 7 :style {:fill :white}}]
   [:circle {:cx 232 :cy 280 :r 3 :style {:fill :black}}]
   [:circle {:cx 250 :cy 280 :r 7 :style {:fill :white}}]
   [:circle {:cx 248 :cy 280 :r 3 :style {:fill :black}}]
   (render-antenna antenna (case shape :square 0 :round -20))])

(defn render-arms [{:keys [::hands ::angle]}]
  [:g
   [:rect {:x 180 :y 310 :width 20 :height 80 :transform (str "rotate("  angle " 190 310)")}]
   [:rect {:x 280 :y 310 :width 20 :height 80 :transform (str "rotate(" (- angle) " 290 310)")}]
   (case hands
     :claw
     [:g]
     :lazor
     [:g])])

;; Rendering

(defn render-bot [{:keys [::torso ::head ::legs ::arms ::color]}]
  [:g {:style {:fill   color
               :stroke :black}}
   (render-legs legs)
   (render-torso torso)
   (render-head head)
   (render-arms arms)]
  )
;; Tests


(defn bot-component []
  (let [bot (r/atom (-> ::robot s/gen generate))]
    (fn []
      [:div
       [:button {:type :button
                 :on-click #(reset! bot (-> ::robot s/gen generate))}
        "Reset!"]
       [:pre (-> @bot pprint with-out-str)]
       [:svg {:width  640
              :height 480}
        (render-bot @bot)]])))

(defcard-rg robo-test
  [bot-component])
