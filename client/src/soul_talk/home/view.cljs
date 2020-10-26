(ns soul-talk.home.view
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [re-frame.core :refer [subscribe]]
            [antd :as antd]
            ["@ant-design/icons" :as antd-icons]
            [soul-talk.common.views :refer [home-layout]]
            [soul-talk.home.db :refer [resources-data contact-data]]))


(defn list-resources [title data]
  [:div.contact-me
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

(defn about []
  [:> antd/Layout.Content
   [:> antd/Row {:gutter 10}
    [:> antd/Col {:xs 24 :sm 24 :md 6 :lg 6}
     (list-resources "相关资源" resources-data)]
    [:> antd/Col {:xs 24 :sm 24 :md 6 :lg 6}
     (list-resources "联系我" contact-data)]]
   [:> antd/Divider]
   ])

(defn banner []
  [:h1 "进一步有一步的欢喜"])

(defn home-page []
  [home-layout
   [:div
    [:div.home-wrapper
     [banner]]
    [:div.home-wrapper-about
     [about]]]])

