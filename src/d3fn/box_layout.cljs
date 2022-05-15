(ns d3fn.box-layout
  (:require [d3fn.kiwi-helper :as k]))

;; Some constraint helpers

(def constraint-fns
  {:=  k/=
   :>= k/>=
   :<= k/<=})

(defn add-constraint [solver constraint]
  (let [[kind & args] constraint
        cfn (constraint-fns kind)]
    (k/add-constraint solver (apply cfn args))))

;; Constraints and higher order combinators

(defn c=  [& args]
  (map #(into [:=]  %) (partition 2 1 args)))
(defn c<= [& args]
  (map #(into [:<=] %) (partition 2 1 args)))
(defn c>= [& args]
  (map #(into [:>=] %) (partition 2 1 args)))

(defn lookup-or-create [state path]
  (if-some [current-value (get-in @state path)]
    current-value
    (let [new-variable (k/make-variable)]
      (swap! state assoc-in path new-variable)
      new-variable)))

(defn lookup-box
  ([state box-key]
   (lookup-box state box-key [:x :y :w :h]))
  ([state box-key key-seq]
   (->> key-seq
        (map (fn [k] [k (lookup-or-create state [box-key k])]))
        (into {}))))

(defn center [{:keys [x y w h]}]
  [(k/+ x (k// w 2)) (k/+ y (k// h 2))])

(def center-x (comp first  center))
(def center-y (comp second center))

(def left :x)
(def top  :y)
(defn right  [{:keys [x w]}] (k/+ x w))
(defn bottom [{:keys [y h]}] (k/+ y h))
(def width  :w)
(def height :h)

(defn fixed-gap [state box-keys gap]
  (let [boxes (map #(lookup-box state % [:x :w]) box-keys)]
    (set
     (for [[a b] (partition 2 1 boxes)]
       [:= (k/- (left b) (right a)) gap]))))

(defn align-bottom [state box-keys]
  (let [boxes (map #(lookup-box state % [:y :h]) box-keys)]
    (set
     (for [[a b] (partition 2 1 boxes)]
       [:= (bottom a) (bottom b)]))))

(defn align-center-x [state box-keys]
  (let [boxes (map #(lookup-box state %) box-keys)]
    (set
     (for [[a b] (partition 2 1 boxes)]
       [:= (center-x a) (center-x b)]))))

(defn align-center-y [state box-keys]
  (let [boxes (map #(lookup-box state %) box-keys)]
    (set
     (for [[a b] (partition 2 1 boxes)]
       [:= (center-y a) (center-y b)]))))

(defn above [state box-keys]
  (let [boxes (map #(lookup-box state % [:y :h]) box-keys)]
    (set
     (for [[a b] (partition 2 1 boxes)]
       [:>= (bottom a) (top b)]))))

(defn layout! [constraints]
  (let [all-constraints (mapcat #(if (set? %) % [%]) constraints)
        solver (k/make-solver)]
    (k/solve (reduce add-constraint solver all-constraints))))

;; Functions for collections

(defn numbered [box-key index]
  (keyword (namespace box-key) (str (name box-key) "." index)))

(defn zip-value [state box-key property target-seq]
  (->> target-seq
       (map-indexed
        (fn [i target]
          [:=
           (lookup-or-create state [(numbered box-key i) property])
           target]))
       set))

(defn get-box-keys [state key-prefix]
  (->> @state
       keys
       (filter #(.startsWith (name %) (name key-prefix)))))

;; Getting the actual data back out

(defn map-values [f m]
  (into {}
        (for [[k v] m]
          [k (f v)])))

(defn realize-all [boxes]
  (map (fn [[k box]]
         [k (map-values #(if (number? %)
                           %
                           (deref %))
                        box)])
       boxes))

;; Rendering

(defn to-rectangle [{:keys [x y w h]} & {:as opts}]
  [:rect (merge {:x x :y y :width w :height h}
                opts)])

(defn render [state]
  (let [values (realize-all @state)]
    (for [[k v] values]
      (case (namespace k)
        "rect" (to-rectangle v)
        nil))))
