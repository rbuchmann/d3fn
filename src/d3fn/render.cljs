(ns d3fn.render)

(defmulti node :type)

(defmethod node :default [{:keys [x y r] :as node}]
  [:circle {:cx x
            :cy y
            :r (or r 5)}])

(defmulti link :type)

(defmethod link :default [{{x1 :x y1 :y} :source {x2 :x y2 :y} :target}]
  [:line {:x1 x1
          :y1 y1
          :x2 x2
          :y2 y2
          :stroke :black}])
