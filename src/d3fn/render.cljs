(ns d3fn.render)

(defmulti primitive-bbox first)

(defmethod primitive-bbox :circle [[_ {radius :r}]]
  [radius radius])

(defmethod primitive-bbox :rectangle [[_ {:keys [width height]}]]
  [width height])

(defmethod primitive-bbox :line [[_ {:keys [x1 x2 y1 y2]}]]
  [(.abs js/Math (- x2 x1)) (.abs js/Math (- y2 y1))])

(defmulti primitive-anchor first)

(defmethod primitive-anchor :circle [_]
  [0.5 0.5])

(defmethod primitive-anchor :rectangle [_]
  [0 0])

(defmulti render-object :type)

(defmethod render-object :node [{:keys [x y r] :as node}]
  [:circle {:cx x
            :cy y
            :r (or r 5)}])

(defmethod render-object :link [{{x1 :x y1 :y} :source {x2 :x y2 :y} :target}]
  [:line {:x1 x1
          :y1 y1
          :x2 x2
          :y2 y2
          :stroke :black}])

(defmethod render-object :default [obj]
  (println "fallthrough: " obj))

(defmulti bbox-object :type)

(defmethod bbox-object :default [{:keys [width height]}]
  [width height])

(defmulti bbox-anchor :type)

(defmethod bbox-anchor :default [_]
  [0 0])

(defprotocol Renderable
  (render [this])
  (bbox [this])
  (anchor [this]))

(extend-protocol Renderable
  cljs.core/PersistentVector
  (render [this] this)
  (bbox [this] (primitive-bbox this))
  (anchor [this] (primitive-anchor this))
  cljs.core/PersistentArrayMap
  (render [this] (render-object this))
  (bbox [this] (bbox-object this))
  (anchor [this] (anchor-object this)))
