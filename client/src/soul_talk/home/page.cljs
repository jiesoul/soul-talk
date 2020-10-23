(ns soul-talk.home.page
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [soul-talk.home.layout :refer [layout banner]]
            [soul-talk.article.component :refer [home-articles]]
            [re-frame.core :refer [subscribe]]
            [antd]
            ["@ant-design/icons" :as antd-icons]))

(def resources-data
  [{:title "Clojure" :href "https://www.clojure.org"}
   {:title "ClojureDocs" :href "https://clojuredocs.org"}
   {:title "codewars" :href "https://www.codewars.com"}
   {:title "The Clojure Toolbox" :href "https://www.clojure-toolbox.com/"}
   {:title "Clojurescript" :href "https://www.clojurescript.org"}
   {:title "React" :href "https://reactjs.org/"}
   {:title "reagent" :href "https://reagent-project.github.io/"}
   {:title "re-frame" :href "https://github.com/Day8/re-frame"}
   {:title "Figwheel Main" :href "https://figwheel.org/"}])

(def contact-data
  [{:title "github" :icon (r/as-element [:> antd-icons/GithubFilled]) :href "https://github.com/jiesoul"}
   {:title "weibo" :icon (r/as-element [:> antd-icons/WeiboCircleFilled]) :href "https://weibo.com/jiesoul"}
   {:title "twitter" :icon (r/as-element [:> antd-icons/TwitterCircleFilled]) :http "https://twitter.com/jiesoul1982"}])

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

(defn home-page []
  (r/with-let [active-page (rf/subscribe [:active-page])]
    (fn []
      [layout
       [:div
        [:div.home-wrapper
         [banner]]
        [:div.home-wrapper-post
         [home-articles]]
        [:div.home-wrapper-about
         [about]]]])))