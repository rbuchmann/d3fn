(defproject d3fn "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.5.3"

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [devcards "0.2.1-5"]
                 [reagent "0.6.0-alpha"]
                 [cljsjs/d3 "3.5.7-1"]
                 [camel-snake-kebab "0.3.2"]
                 [org.clojure/core.async "0.2.374"]]

  :plugins [[lein-figwheel "0.5.0-6"]
            [lein-cljsbuild "1.1.2" :exclusions [org.clojure/clojure]]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]

  :source-paths ["src" "test"]

  :cljsbuild {
              :builds [{:id "devcards"
                        :source-paths ["src" "test"]
                        :figwheel { :devcards true } ;; <- note this
                        :compiler { :main       "d3fn.cards"
                                    :asset-path "js/compiled/devcards_out"
                                    :output-to  "resources/public/js/compiled/d3fn_devcards.js"
                                    :output-dir "resources/public/js/compiled/devcards_out"
                                    :source-map-timestamp true }}
                       {:id "dev"
                        :source-paths ["src"]
                        :figwheel true
                        :compiler {:main       "d3fn.core"
                                   :asset-path "js/compiled/out"
                                   :output-to  "resources/public/js/compiled/d3fn.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :source-map-timestamp true }}
                       {:id "prod"
                        :source-paths ["src"]
                        :compiler {:main       "d3fn.core"
                                   :asset-path "js/compiled/out"
                                   :output-to  "resources/public/js/compiled/d3fn.js"
                                   :optimizations :advanced}}]}

  :figwheel { :css-dirs ["resources/public/css"] })
