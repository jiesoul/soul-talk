(ns soul-talk.home.layout
  (:require [soul-talk.common.component :refer [header footer]]
            [soul-talk.routes :refer [navigate!]]
            [reagent.core :as r]
            [re-frame.core :as rf]))

(defn nav [active-page]
  [:> js/antd.Menu {:className         "home-nav"
                    :mode              "horizontal"
                    :theme "dark"
                    :defaultSelectKeys ["home"]
                    :selectedKeys      [(key->js active-page)]}
   [:> js/antd.Menu.Item {:key      "home"
                          :on-click #(navigate! "#/")}
    "首页"]
   [:> js/antd.Menu.Item {:key      "blog"
                          :on-click #(navigate! "#/blog")}
    "博客"]])

(defn banner []
  [:h1 "进一步有一步的欢喜"])

(defn layout [children]
  (r/with-let [active-page (rf/subscribe [:active-page])]
    (fn []
      [:> js/antd.Layout
       [header [nav @active-page]]
       [:> js/antd.Layout.Content
        children]
       [footer]])))