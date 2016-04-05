(ns d3fn.core
  (:require
   [reagent.core :as reagent]
   cljsjs.d3)
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest defcard-rg]]))

(enable-console-print!)

(defn main []
  ;; conditionally start the app based on whether the #main-app-area
  ;; link is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (reagent/render-component [:div "This is working"] node)))

(main)

;; remember to run lein figwheel and then browse to
;; http://localhost:3449/cards.html
