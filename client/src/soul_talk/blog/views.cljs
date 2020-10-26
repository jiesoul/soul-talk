(ns soul-talk.blog.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [soul-talk.common.views :refer [home-layout]]
            [soul-talk.article.views :refer [blog-articles blog-archives blog-archives-articles]]
            [antd :as antd]))

(defn blog-page []
  (r/with-let [active-page (rf/subscribe [:active-page])]
    (fn []
      [home-layout
       [:div.home-wrapper-page1
        [:> antd/Row {:gutter 16}
         [:> antd/Col {:span 16 :offset 2}
          [blog-articles]]
         [:> antd/Col {:span 4}
          [blog-archives]]]]])))

(defn blog-archives-page []
  (r/with-let [active-page (rf/subscribe [:active-page])]
    [home-layout
     [:div.home-wrapper-page1
      [:> antd/Row {:gutter 16}
       [:> antd/Col {:span 16 :offset 2}
        [blog-archives-articles]]
       [:> antd/Col {:span 4}
        [blog-archives]]]]]))
