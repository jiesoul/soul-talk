(ns soul-talk.dash.page
  (:require [reagent.core :as r]
            [soul-talk.common.layout :refer [basic-layout]]))

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

(defn complex-component [a b c]
  (let [state (r/atom {})]
    (r/create-class
      {:component-did-mount
       (fn [comp]
         (js/console.log comp))

       :display-name "complex-component"

       :reagent-render
       (fn [a b c]
         [:div {:class c}
          [:i a] " " b])})))

(defn dash-page []
  (fn []
    [basic-layout
     [:div
      [:h1.h2 "Dashboard"]
      [:div.btn-toolbar.mb-2.mb-md-0
       [:div.btn-group.mr-2
        [:button.btn.btn-sm.btn-outline-secondary "Share"]
        [:button.btn.btn-sm.btn-outline-secondary "Export"]
        [complex-component "a" "b" "c"]]]]]))