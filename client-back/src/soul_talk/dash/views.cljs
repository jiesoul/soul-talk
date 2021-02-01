(ns soul-talk.dash.views
  (:require [reagent.core :as r]
            [soul-talk.common.views :as c]))

(defn chart-styles [theme]
  #js {:root #js {:backgroundColor "red"}})

(defn chart []
  [:div
   "ssdasfasdfasdff"])

(defn home []
  [c/layout [chart]])