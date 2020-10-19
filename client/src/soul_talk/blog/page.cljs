(ns soul-talk.blog.page
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [soul-talk.home.layout :refer [layout]]
            [soul-talk.article.component :refer [blog-articles blog-archives blog-archives-articles]]))

(defn blog-page []
  (r/with-let [active-page (rf/subscribe [:active-page])]
    (fn []
      [layout
       [:div.home-wrapper-page1
        [:> js/antd.Row {:gutter 16}
         [:> js/antd.Col {:span 16 :offset 2}
          [blog-articles]]
         [:> js/antd.Col {:span 4}
          [blog-archives]]]]])))

(defn blog-archives-page []
  (r/with-let [active-page (rf/subscribe [:active-page])]
    [layout
     [:div.home-wrapper-page1
      [:> js/antd.Row {:gutter 16}
       [:> js/antd.Col {:span 16 :offset 2}
        [blog-archives-articles]]
       [:> js/antd.Col {:span 4}
        [blog-archives]]]]]))
