(ns soul-talk.core
  (:require [soul-talk.login :as login]
            [soul-talk.register :as register]
            [soul-talk.dash :as dash]
            [reagent.core :as r]
            [reagent.session :as session]
            [domina :as dom]
            [taoensso.timbre :as log]
            [cuerdas.core :as str]))

(defonce posts (r/atom []))
(defonce navs (r/atom []))
(defonce archives (r/atom []))

(defn log-component? [name]
  (fn []
    [:div
     [:span.navbar-text (str "欢迎你 " name "")]
     [:a.btn.btn-sm.btn-outline-secondary {:href "/logout"} "退出"]]))

(defn blog-header-component []
  (fn []
    [:div.blog-header.py-3
     [:div.row.flex-nowrap.justify-content-between.align-items-center
      [:div.col-4.pt-1
       [:a.text-muted {:href "#"} "订阅"]]
      [:div.col-4.text-center
       [:a.blog-header-logo.text-dark {:href "/"} "Soul Talk"]]
      [:div.col-4.d-flex.justify-content-end.align-items-center
       (if-not (= js/identity "")
         [log-component? js/identity]
         [:a.btn.btn-sm.btn-outline-secondary {:href "/login"} "登录"])]]]))

(defn nav-scroller-header-component [navs]
  (fn []
    [:div.nav-scroller.py-1.mb-2
     [:nav.nav.d-flex.justify-content-between
      (for [{:keys [href value] :as nav} navs]
        ^{:key nav} [:a.p-2.text-muted {:href href :id value} value])]]))

(defn jumbotron-header-component []
  (fn []
    [:div.jumbotron.p-3.p-md-5.text-white.rounded.bg-dark
     [:div.col-md-6.px-0
      [:h1.display-4.font-italic "Title of a longer featured blog post"]
      [:p.lead.mb-0
       [:a.text-white.font-weight-bold {:href "#"} "Continue reading..."]]]]))

(defn header-component []
  (fn []
    [:div.container
     [blog-header-component]
     [nav-scroller-header-component @navs]
     [jumbotron-header-component]]))

(defn footer-component []
  (fn []
    [:div.container.blog-footer
     [:p "Blog template built for"
      [:a {:href "https://getbootstrap.com/"} "Bootstrap"]
      " by "
      [:a {:href "https://twitter.com/mdo"} "@mdo"]
      "."]
     [:p
      [:a {:href "#"} "Back to top"]]]))

(defn blog-post-component [posts]
  (fn []
    [:div.col-md-8.blog-main
     [:h3.pb-3.mb-4.font-italic.border-bottom
      "From the Firehose"]
     (for [{:keys [id title meta author content] :as post} posts]
       ^{:key post} [:div.blog-post
                     [:h2.blog-post-title title]
                     [:p.blog-post-meta meta
                      [:a {:href "#" :id id} author]]
                     [:p content]])
     [:nav.blog-pagination
      [:a.btn.btn-outline-primary {:href "#"} "Older"]
      [:a.btn.btn-outline-secondary.disabled {:href "#"} "Newer"]]]))

(defn main-component []
  (fn []
    [:div.container {:role "main"}
     [:div.row
      [blog-post-component @posts]
      [:aside.col-md-4.blog-sidebar
       [:div.p-3.mb-3.bg-light.rounded
        [:h4.font-italic "About"]
        [:p.mb-0 "Etiam porta <em>sem malesuada magna</em> mollis euismod."]]
       [:div.p-3
        [:h4.font-italic "Archives"]
        [:ol.list-unstyled.mb-0
         (for [{:keys [time href] :as archive} @archives]
           ^{:key archive} [:li [:a {:href href} time]])]]
       [:div.p-3
        [:h4.font-italic "Elsewhere"]
        [:ol.list-unsty
         [:li [:a {:href "#"} "GitHub"]]
         [:li [:a {:href "#"} "Weibo"]]
         [:li [:a {:href "#"} "Twitter"]]]]]]]))

(defn home-component []
  [:div
   [header-component]
   [main-component]
   [footer-component]])

(reset! navs [{:href "#"
                :value "World"}
              {:href "#"
               :value "China"}
              {:href "#"
               :value "China1"}
              {:href "#"
               :value "China2"}])

(reset! posts [{:id "post1"
               :title   "Sample blog post"
               :meta    "January 1, 2014 by"
               :author  "soul"
               :content "asasfasfasffsd"}
              {:id "post2"
               :title   "Another blog post"
               :meta    "December 23, 2013 by "
               :author  "jiesoul"
               :content "Cum sociis natoque penatibus et magnis"}])

(reset! archives [{:href "#"
                    :time "March 2018"}
                  {:href "#"
                   :time "May 2018"}])

(defn ^:export init []
  (if (and js/document
           (.-getElementById js/document))
    (r/render
      [home-component]
      (dom/by-id "app"))))