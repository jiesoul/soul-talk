(ns soul-talk.pages.admin
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [cljsjs.chartjs])
  (:import goog.history.Html5History))

(defonce main-fields (r/atom nil))
(defonce table-data (r/atom []))

(defn table-component []
  (fn []
    [:div
     [:h2 "Section title"]
     [:div.table-responsive
      [:table.table.table-striped.table-sm
       [:thead
        [:tr
         [:th "#"]
         [:th "Header"]
         [:th "Header"]
         [:th "Header"]
         [:th "Header"]]]
       [:tbody]]]]))

(defn main-component []
  [:div
   [table-component]])