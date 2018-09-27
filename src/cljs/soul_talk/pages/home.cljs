(ns soul-talk.pages.home
  (:require [reagent.core :as r]
            [re-frame.core :refer [dispatch subscribe]]
            [soul-talk.pages.common :as c])
  (:import [goog.history.Html5History]))

(defn blog-header-component []
  (fn []
    [:div.blog-header.py-3
     [:div.row.flex-nowrap.justify-content-between.align-items-center
      [:div.col-4.pt-1
       [:a.text-muted {:href "#"} "Subscribe"]]
      [:div.col-4.text-center
       [:a.blog-header-logo.text-dark {:href "/"} "Soul Talk"]]
      [:div.col-4.d-flex.justify-content-end.align-items-center
       [:a.text-muted {:href "#"} "About"]]]]))

(defn nav-scroller-header-component []
  (r/with-let [navs (subscribe [:categories])]
              (fn []
                [:div.nav-scroller.py-1.mb-2
                 [:nav.nav.d-flex.justify-content-between
                  (for [{:keys [id name] :as nav} @navs]
                    ^{:key nav}
                    [:a.p-2.text-muted {:href (str id)} name])]])))

(defn jumbotron-header-component []
  (fn []
    [:div.jumbotron.p-3.p-md-5.text-white.rounded.bg-dark
     [:div.col-md-6.px-0.text-center
      [:h2.display-4.font-italic "进一步有一步的欢喜"]]]))

(defn header-component []
  (fn []
    [:div.container
     [blog-header-component]
     [nav-scroller-header-component]
     [jumbotron-header-component]]))

(defn footer-component []
  (fn []
    [:div.container.blog-footer
     [:p
      [:a.text-muted {:href "#"} "Back to top"]]]))

(defn blog-post-component []
  (r/with-let [posts (subscribe [:posts])
               pagination (subscribe [:pagination])
               offset (r/cursor pagination [:offset])
               page (r/cursor pagination [:page])
               prev-page (r/cursor pagination [:previous])
               next-page (r/cursor pagination [:next])
               pre-page (r/cursor pagination[:pre-page])]
              (fn []
                [:div.col-md-8.blog-main
                 [:h3.pb-3.mb-4.font-italic.border-bottom
                  "文章"]
                 (for [{:keys [id title create_time author content] :as post} @posts]
                   ^{:key post} [:div.blog-post
                                 [:h2.blog-post-title
                                  [:a.text-muted
                                   {:href (str "/posts/" id)
                                    :target "_blank"}
                                   title]]
                                 [:p.blog-post-meta (str (.toDateString (js/Date. create_time)) " by " author)]
                                 [:hr]
                                 [:div [c/markdown-preview content]]])
                 [:nav.blog-pagination
                  [:a.btn.btn-outline-primary
                   {:on-click #(dispatch [:load-posts {:page @next-page
                                                       :pre-page @pre-page}])}
                   "Older"]
                  [:a.btn.btn-outline-secondary
                   {:on-click #(dispatch [:load-posts {:page @prev-page
                                                       :pre-page @pre-page}])
                    :class (if (zero? @offset) "disabled")}
                   "Newer"]]])))

(defn where-component []
  (fn []
    [:div.p-3
     [:h4.font-italic "联系我"]
     [:ol.list-unsty
      [:li [:a {:href "https://github.com/jiesoul"
                :target "_blank"}
            [:i.fab.fa-github]
            " GitHub"]]
      [:li [:a {:href "https://weibo.com/jiesoul"
                :target "_blank"}
            [:i.fab.fa-weibo] " Weibo"]]
      [:li [:a {:href "https://twitter.com/jiesoul1982"
                :target "_blank"}
            [:i.fab.fa-twitter]
            " Twitter"]]]]))

(defn archives-component []
  (r/with-let [posts-archives (subscribe [:posts-archives])]
    (fn []
      [:div.p-3
       [:h4.font-italic "Archives"]
       [:ol.list-unstyled.mb-0
        (for [{:keys [year month] :as archive} @posts-archives]
          ^{:key archive}
          [:li [:a {:href ""} (str month " " year)]])]])))

(defn main-component []
  (fn []
    [:div.container {:role "main"}
     [:div.row
      [blog-post-component]
      [:aside.col-md-4.blog-sidebar
       [:div.p-3.mb-3.bg-light.rounded
        [:h4.font-italic "About"]
        [:p.mb-0 ""]]
       [where-component]
       [archives-component]]]]))

(defn home-component []
  [:div
   [header-component]
   [main-component]
   [footer-component]])

(defn home-page []
  [home-component])