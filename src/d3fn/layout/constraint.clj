(ns d3fn.layout.constraint
  (:require [d3fn.layout.constraint-protocol :as cp]))

(defmacro let-c [bindings & body]
  (let [b-pairs     (partition 2 bindings)
        constraints (gensym "constraints")
        more-constraints (->> body butlast vec)
        return-value (last body)]
    `(let [~constraints []
           ~@(mapcat (fn [[lhs rhs]]
                       (let [nc (gensym "new-constraints")]
                         [[lhs nc]    `(cp/result ~rhs)
                          constraints `(concat ~constraints ~nc)]))
                     b-pairs)]
       (cp/with-constraints ~return-value (apply concat ~constraints ~more-constraints)))))
