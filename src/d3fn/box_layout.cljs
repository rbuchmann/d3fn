(ns d3fn.box-layout
  (:require [d3fn.kiwi-helper :as k]
            [clojure.set      :as set]))

;; Protocol to allow returning one or many constraints and box-paths

(defprotocol Expandable
  (get-items [this]))

(extend-protocol Expandable
  cljs.core/LazySeq
  (get-items [col] col))

(defn many [col]
  (reify Expandable
    (get-items [_] col)))

(defn expand [x]
  (if (satisfies? Expandable x)
    (get-items x)
    [x]))

(defn to-path [x]
  (assert (or (keyword? x) (vector? x)) "path must be a vector or a keyword!")
  (if (keyword? x)
    [x]
    x))

(defn expand-all [col]
  (mapcat expand col))

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

(defn lookup-or-create [state path]
  (if-some [current-value (get-in @state path)]
    current-value
    (let [new-variable (k/make-variable)]
      (swap! state assoc-in path new-variable)
      new-variable)))

(defn lookup-box
  ([state box-path]
   (lookup-box state box-path [:x :y :w :h]))
  ([state box-path key-seq]
   (->> key-seq
        (map (fn [k] [k (lookup-or-create state (conj (to-path box-path) k))]))
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

(defn horizontal-gap [gap & box-paths]
  (fn [state]
    (let [boxes (map #(lookup-box state % [:x :w]) (expand-all box-paths))]
      (for [[a b] (partition 2 1 boxes)]
        [:= (k/- (left b) (right a)) gap]))))

(defn vertical-gap [gap & box-paths]
  (fn [state]
    (let [boxes (map #(lookup-box state % [:y :h]) (expand-all box-paths))]
      (for [[a b] (partition 2 1 boxes)]
        [:= (k/- (top b) (bottom a)) gap]))))

(defn align-bottom [& box-paths]
  (fn [state]
    (let [boxes (map #(lookup-box state % [:y :h]) (expand-all box-paths))]
      (for [[a b] (partition 2 1 boxes)]
        [:= (bottom a) (bottom b)]))))

(defn align-center-x [& box-paths]
  (fn [state]
    (let [boxes (map #(lookup-box state %) (expand-all box-paths))]
      (for [[a b] (partition 2 1 boxes)]
        [:= (center-x a) (center-x b)]))))

(defn align-center-y [& box-paths]
  (fn [state]
    (let [boxes (map #(lookup-box state %) (expand-all box-paths))]
      (for [[a b] (partition 2 1 boxes)]
        [:= (center-y a) (center-y b)]))))

(defn above [& box-paths]
  (fn [state]
    (let [boxes (map #(lookup-box state % [:y :h]) (expand-all box-paths))]
      (print boxes)
      (for [[a b] (partition 2 1 boxes)]
        [:>= (bottom a) (top b)]))))

(defn fix-value [box-path v]
  (fn [state]
    [:= (lookup-or-create state box-path) v]))

(defn layout!
  ([constraints]
   (layout! (atom {}) constraints))
  ([state constraints]
   (let [all-constraints (mapcat #(expand (% state)) constraints)
         solver (k/make-solver)]
     (k/solve (reduce add-constraint solver all-constraints))
     state)))

;; Functions for collections

(defn zip-value [col-path property target-seq]
  (fn [state]
    (map-indexed
     (fn [i target]
       [:=
        (lookup-or-create state (concat (to-path col-path) [i property]))
        target])
     target-seq)))

(defn all-box-paths [col-path n]
  (let [full-path (to-path col-path)]
    (assert (vector? full-path) "Col path needs to be a vector")
    (map #(conj full-path %) (range n))))

;; Getting the actual data back out

(defn map-values [f m]
  (into {}
        (for [[k v] m]
          [k (f v)])))

(defn box? [m]
  (and (map? m)
       (set/subset? (set (keys m)) #{:x :y :w :h})))

(defn realize-all
  ([boxes]
   (realize-all [] boxes))
  ([current-path boxes]
   (mapcat (fn [[k v]]
             (let [new-path (conj current-path k)]
                (cond
                  (box? v) [[new-path (map-values #(if (number? %) % (deref %)) v)]]
                  :else (realize-all new-path v))))
           boxes)))

;; Rendering

(defmulti render-box (comp keyword namespace first first))
(defmethod render-box :rect [[_ {:keys [x y w h]}]]
  [:rect {:x x :y y :width w :height h}])

(defmethod render-box :hline [[_ {:keys [x y w] :or {x 0 y 0 w 0}}]]
  [:line {:x1 x :x2 (+ x w) :y1 y :y2 y
          :stroke :black
          :stroke-width 0.25}])

(defmethod render-box :vline [[_ {:keys [x y h]}]]
  [:line {:x1 x :x2 x :y1 y :y2 (+ y h)
          :stroke :black
          :stroke-width 0.25}])

(defn render [state]
  (let [values (realize-all @state)]
    (map render-box values)))
