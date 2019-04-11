(ns d3fn.layout.constraint
  (:require [d3fn.layout.kiwi-helper         :as k]
            [d3fn.layout.constraint-protocol :as cp]
            [devcards.core                   :as dc :refer-macros [defcard-rg]])
  (:require-macros [d3fn.layout.constraint  :refer [let-c]]))

(extend-protocol cp/SolverRelevant
  object
  (result [this] [this []]))

;; Some constraint helpers

(def constraint-fns
  {:=  k/=
   :>= k/>=
   :<= k/<=})

(defn add-variable [solver [variable value strength]]
  (k/add-edit-variable solver variable (or strength :strong))
  (k/suggest-value solver variable value)
  solver)

(defn add-constraint [solver constraint]
  (let [[kind & args] constraint]
    (if (= :var kind)
      (add-variable solver args)
      (let [cfn (constraint-fns kind)]
        (k/add-constraint solver (apply cfn args))))))

(defn wrap-variable [x]
  (let [v (k/make-variable)]
    (cond
      (nil? x) v
      (number? x) (cp/with-constraint v [:var v x])
      (vector? x) (cp/with-constraint v (into [:var v] (reverse x)))
      ;; The reverse here allows us to write [:strong 42] which feels
      ;; more natural. It's the other way around in add-variable to
      ;; make strength optional
      )))

;; Constraints and higher order combinators

(defn c=  [& args]
  (map #(into [:=]  %) (partition 2 1 args)))
(defn c<= [& args]
  (map #(into [:<=] %) (partition 2 1 args)))
(defn c>= [& args]
  (map #(into [:>=] %) (partition 2 1 args)))

(defn transpose [col]
  (apply map list col))

(defn map-constraints [f col]
  (let [[results constraints] (apply map list (map (comp cp/result f) col))]
    (cp/with-constraints results (apply concat constraints))))

(defn constrain-all [cfn col]
  ())

;; The meaty part of the layout algorithm

(defn box [& {:keys [x y w h]}]
  (let-c [[x y w h] (map-constraints wrap-variable [x y w h])]
    (c>= x (k/make-expression 0))
    (c>= y (k/make-expression 0))
    {:x x
     :y y
     :w w
     :h h}))

(def left :x)
(def top  :y)
(defn right  [{:keys [x w]}] (k/+ x w))
(defn bottom [{:keys [y h]}] (k/+ y h))
(def width  :w)
(def height :h)

(defn center [{:keys [x y w h]}]
  [(k/+ x (k// w 2)) (k/+ y (k// h 2))])

(defn fixed-gap [boxes gap]
  (for [[a b] (partition 2 1 boxes)]
    [:= (k/- (left b) (right a)) gap]))

(defn align-bottom [boxes]
  (for [[a b] (partition 2 1 boxes)]
    [:= (bottom a) (bottom b)]))

(defn layout [v]
  (let [[value constraints] (cp/result v)
        solver (k/make-solver)]
    (k/solve (reduce add-constraint solver constraints))
    value))

;; Rendering

(defn to-rectangle [{:keys [x y w h]}]
  [:rect {:x @x :y @y :width @w :height @h}])

;; Tests

(defcard-rg testing-layout
  [:div
   [:h1 "Barchart"]
   [:svg {:width  800
          :height 600}
    (into [:g]
          (layout
            (let-c [bars (map-constraints #(box :w 20 :h %) [40 20 70 90])]
              (fixed-gap bars 20)
              (align-bottom bars)
              (map to-rectangle bars))))]])
