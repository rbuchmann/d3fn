(ns d3fn.layout.constraint-test
  (:require [d3fn.layout.constraint :as c]
            [cljs.test              :as t :include-macros true :refer [is]]
            [devcards.core          :as dc :refer-macros [deftest defcard-rg]]))

(defcard-rg foo
  [:div
   [:h1 "Foo"]
   [:p (str (c/transpose [[1 2] [3 4]]))]
   [:p (pr-str (c/box :w 5))]])
