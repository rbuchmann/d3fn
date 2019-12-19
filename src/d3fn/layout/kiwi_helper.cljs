(ns d3fn.layout.kiwi-helper
  (:require cljsjs.kiwijs)
  (:refer-clojure :exclude [= >= <= + - * / max min]))

;; Wrappers and constructors

(defn make-solver []
  (js/kiwi.Solver.))

(defn make-variable
  ([] (js/kiwi.Variable.))
  ([name] (js/kiwi.Variable. name)))

(defn make-variables [n]
  (repeatedly n make-variable))

(defn make-expression
  ([x] (js/kiwi.Expression. x))
  ([x y] (js/kiwi.Expression. x y))
  ([x y z] (js/kiwi.Expression. x y z)))

(defn wrap-number [x]
  (cond-> x
    (number? x) make-expression))

(defn make-constraint [operator lhs rhs]
  (js/kiwi.Constraint. (js/kiwi.Expression. (wrap-number lhs) #js [-1 (wrap-number rhs)]) operator))

(def strengths {:weak     js/kiwi.Strength.weak
                :medium   js/kiwi.Strength.medium
                :strong   js/kiwi.Strength.strong
                :required js/kiwi.Strength.required})

;; Make variables derefable

(extend-protocol IDeref
  js/kiwi.Variable
  (-deref [this] (.value this)))

;; Solver tools

(defn suggest-value [solver variable value]
  (.suggestValue solver variable value)
  solver)

(defn add-constraint [solver constraint]
  (.addConstraint solver constraint)
  solver)

(defn add-edit-variable [solver variable strength]
  (.addEditVariable solver variable (strengths strength))
  solver)

(defn add-edit-variables [solver vars strength]
  (reduce (fn [solver variable]
            (add-edit-variable solver variable strength))
          solver vars))

(defn solve [solver]
  (.updateVariables solver)
  solver)

;; Constraints

(def  = (partial make-constraint js/kiwi.Operator.Eq))
(def >= (partial make-constraint js/kiwi.Operator.Ge))
(def <= (partial make-constraint js/kiwi.Operator.Le))

;; Operators

(defn + [a b]
  (.plus a b))

(defn - [a b]
  (.minus a b))

(defn * [a b]
  (.multiply a b))

(defn / [a b]
  (.divide a b))

;; Higher level constraints To Be Moved

(defn max [solver & args]
  (let [m (make-variable)]
    (doseq [v args]
      (add-constraint solver (>= m v)))
    m))

(defn min [solver & args]
  (let [m (make-variable)]
    (doseq [v args]
      (add-constraint solver (<= m v)))
    m))

(defn center [solver [x y] [w h]]
  (let [[cx cy] (repeatedly 2 make-variable)]
    (-> solver
        (add-constraint (= cx (+ x (/ w 2))))
        (add-constraint (= cy (+ y (/ h 2)))))
    [cx cy]))
