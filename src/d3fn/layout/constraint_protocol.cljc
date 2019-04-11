(ns d3fn.layout.constraint-protocol
  "A protocol to allow functions to additionally return constraints when
  required and assoicated helpers. The result function will return a
  vector of the value and associated constraints, which can also be
  variable definitions")

(defprotocol SolverRelevant
  (result [_]))

(defrecord ConstraintsPair [value constraints]
  SolverRelevant
  (result [_] [value constraints]))

(def with-constraints ->ConstraintsPair)

(defn with-constraint [x c]
  (ConstraintsPair. x [c]))
