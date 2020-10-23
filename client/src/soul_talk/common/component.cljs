(ns soul-talk.common.component
  (:require [soul-talk.routes :refer [navigate!]]
            [antd :as antd]))

(defn logo []
  [:div.logo
   [:a {:on-click #(navigate! "#/")}
    [:h1 "JIESOUL的个人网站"]]])

(defn header [nav]
  [:> antd/Layout.Header
   [:> antd/Row
    [:> antd/Col {:xs 24 :sm 24 :md 8 :lg 8}
     [logo]]
    [:> antd/Col {:xs 24 :sm 24 :md 16 :lg 16
                     :style {:textAlign "right"}}
     nav]]])

(defn header-dropdown [menu title]
  [:> antd/Dropdown {:overlay menu
                        :style {:color "#000"}}
   [:a {:className "ant-dropdown-link"
        :href "#"}
    [:> js/antd.Icon {:type "user"}]
    "  " title]
   ])

(defn footer []
  [:> antd/Layout.Footer {:style {:textAlign "center"}}
   [:h4 {:style {:color "#FFF"}}
    "Made with By "
    [:a
     {:type   "link"
      :href   "https://ant.design"
      :target "_blank"}
     "Ant Design"]
    " and JIESOUL "]])
