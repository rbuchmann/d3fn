(ns d3fn.helpers-test
  (:require [d3fn.helpers :as helpers]
            [cljs.test :as test :refer-macros [is]]
            [devcards.core :as dc :refer-macros [defcard deftest defcard-rg]]))

(deftest mapping-test
  (is (= (helpers/mapping-map :foo :bar) 5)))
