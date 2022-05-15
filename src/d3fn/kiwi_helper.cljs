(ns d3fn.kiwi-helper
  (:require ["kiwi.js" :as kiwi :refer [Expression Variable Solver Constraint Strength Operator]])
  (:refer-clojure :exclude [= >= <= + - * / max min]))

;; Wrappers and constructors

(defn make-solver []
  (Solver.))

(defn make-variable
  ([] (Variable.))
  ([name] (Variable. name)))

(defn make-variables [n]
  (repeatedly n make-variable))

(defn make-expression
  ([x] (Expression. x))
  ([x y] (Expression. x y))
  ([x y z] (Expression. x y z)))

(defprotocol Expressionistic
  (as-expression [this]))

(extend-protocol Expressionistic
  Variable
  (as-expression [this] (make-expression this))
  Expression
  (as-expression [this] this)
  number
  (as-expression [this] (make-expression this)))

(defn make-constraint [operator lhs rhs]
  (Constraint. (as-expression lhs) operator (as-expression rhs)))

(def strengths {:weak     (-> Strength .-weak)
                :medium   (-> Strength .-medium)
                :strong   (-> Strength .-strong)
                :required (-> Strength .-required)})

;; Make variables derefable

(extend-protocol IDeref
  Variable
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

(def  = (partial make-constraint (.-Eq Operator)))
(def >= (partial make-constraint (.-Ge Operator)))
(def <= (partial make-constraint (.-Le Operator)))

;; Operators

(defn + [a b]
  (.plus (as-expression a) (as-expression b)))

(defn - [a b]
  (.minus (as-expression a) (as-expression b)))

(defn * [a b]
  (.multiply (as-expression a) (as-expression b)))

(defn / [a b]
  (.divide (as-expression a) (as-expression b)))

;; Higher level constraints

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
  (let [[cx cy] (make-variables 2)]
    (-> solver
        (add-constraint (= cx (+ x (/ w 2))))
        (add-constraint (= cy (+ y (/ h 2)))))
    [cx cy]))
