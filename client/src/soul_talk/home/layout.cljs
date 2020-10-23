(ns soul-talk.home.layout
  (:require [soul-talk.common.component :refer [header footer]]
            [soul-talk.routes :refer [navigate!]]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [antd :as antd]))

(defn nav [active-page]
  [:> antd/Menu {:className         "home-nav"
                    :mode              "horizontal"
                    :theme "dark"
                    :defaultselectkeys ["home"]
                    :selected-keys      [(key->js active-page)]}
   [:> antd/Menu.Item {:key "home" :on-click #(navigate! "#/")} "首页"]
   [:> antd/Menu.Item {:key "blog" :on-click #(navigate! "#/blog")} "日志"]
   [:> antd/Menu.Item {:key "resource" :on-click #(navigate! "#/resources")} "资源"]])

(defn banner []
  [:h1 "进一步有一步的欢喜"])

(defn layout [children]
  (r/with-let [active-page (rf/subscribe [:active-page])]
    (fn []
      [:> antd/Layout
       [header [nav @active-page]]
       [:> antd/Layout.Content
        children]
       [footer]])))