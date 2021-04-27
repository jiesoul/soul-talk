(ns soul-talk.pages
  (:require [reagent.core :as r]
            [soul-talk.common.views :as c]
            ["semantic-ui-react" :refer [Container Segment Header Image Button
                                         List Divider Icon]]))

(defn chart-styles [theme]
  #js {:root #js {:backgroundColor "red"}})

(defn chart []
  [:div
   "ssdasfasdfasdff"])

(defn home []
  [:<>
   [c/app-bar]
   [:> Segment {:placeholder true
                :style {:min-height "80vh"}}
    [:> Header {:icon true}
     [:> Icon {:name "pdf file outline"}]
     "asdfasfasfasfasfasf"]
    [:> Button {:content "more"}]]
   [:> Segment {
                :placeholder true}
    "ssdfasfasf"]
   [c/footer]])


(defn articles []
  [:<>
   [c/app-bar]
   [:> Container
    [:> List {:divided true
              :relaxed true}
     [:> List.Item
      [:> List.Content
       [:> List.Header {:as "a"} "文章1111111111111111111111111111111111"]
       [:> List.Description {:as "a"} "2021-3-1 11:11:13 by jiesoul"]]]]]])

(defn series []
  [:<>
   [c/app-bar]
   [:> Segment {:placeholder true
                :basic true
                :inverted true}
    [:> Header {:icon true}
     "series"]
    [:> Button {:content "more"}]]])


(defn tags []
  [:<>
   [c/app-bar]
   [:> Segment {:placeholder true
                :basic true
                :inverted true
                }
    [:> Header {:icon true}
     "Tags"]
    [:> Button {:content "more"}]]])


(defn about []
  [:<>
   [c/app-bar]
   [:> Segment {:placeholder true
                :basic true
                :inverted true}
    [:> Header {:icon true}
     "About"]
    [:> Button {:content "more"}]]])