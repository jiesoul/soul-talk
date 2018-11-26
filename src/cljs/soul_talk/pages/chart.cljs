(ns soul-talk.pages.chart
  (:require [reagent.core :as r]))

(defn show-revenue-chart
  []
  (let [context (.getContext (.getElementById js/document "rev-chartjs") "2d")
        chart-data {:type "bar"
                    :data {:labels ["2012" "2013" "2014" "2015" "2016"]
                           :datasets [{:data [5 10 15 20 25]
                                       :label "Rev in MM"
                                       :backgroundColor "#90EE90"}
                                      {:data [3 6 9 12 15]
                                       :label "Cost in MM"
                                       :backgroundColor "#F08080"}]}}]
    (js/Chart. context (clj->js chart-data))))

(defn canvas-component
  []
  (r/create-class
    {:component-did-mount #(show-revenue-chart)
     :display-name        "chartjs-component"
     :reagent-render      (fn []
                            [:canvas {:id "rev-chartjs" :width "900" :height "300"}])}))

(defn dashboard-component []
  (fn []
    [:div.d-flex.justify-content-between.flex-wrap.flex-md-nowrap.align-items-center.pt-3.pb-2.mb-3.border-bottom
     [:h1.h2 "Dashboard"]
     [:div.btn-toolbar.mb-2.mb-md-0
      [:div.btn-group.mr-2
       [:button.btn.btn-sm.btn-outline-secondary "Share"]
       [:button.btn.btn-sm.btn-outline-secondary "Export"]
       ]]]))