(ns soul-talk.home.view
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [re-frame.core :refer [subscribe]]
            [antd :as antd]
            ["@ant-design/icons" :as antd-icons]
            [soul-talk.common.views :refer [home-layout home-row-col]]
            [soul-talk.home.db :refer [resources-data contact-data]]))


(defn list-resources [title data]
  [:section
   [:h3.contact-me-title title]
   (for [{:keys [title icon href]} data]
     ^{:key title}
     [:div
      [:> antd/Button
       {:href   href
        :target "_blank"
        :icon   icon
        :type   "link"}
       title]])])

(def about-col-option
  {:xs 24 :sm 24 :md 12 :lg 12})

(defn about []
  [:> antd/Layout.Content
   [:> antd/Row
    [:> antd/Col about-col-option
     (list-resources "相关资源" resources-data)]
    [:> antd/Col about-col-option
     (list-resources "联系我" contact-data)]]
   ])

(defn banner []
  [:section
   [:h1 "进一步有一步的欢喜"]])

(defn home-page []
  [home-layout
   [:<>
    [home-row-col
     [:> antd/Carousel {:autoPlay true}
      [:div
       [:h3 {:className "home-carousel-content"}
        "进一步有一步的欢喜"]]
      [:div {:className "home-carousel-content"}
       [banner]]
      [:div
       [:h3 {:className "home-carousel-content"} "3"]]
      [:div
       [:h3 {:className "home-carousel-content"} "4"]]]]
    [:div
     [:div.home-wrapper-about
      [about]]]]])

