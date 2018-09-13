(ns soul-talk.pages.dash
  (:require [reagent.core :as r]
            [domina :as dom]
            [domina.xpath :as p]
            [soul-talk.pages.common :as c]
            [soul-talk.pages.login :as login]
            [soul-talk.pages.register :as reg]
            [cljsjs.chartjs]
            [reagent.session :as session]
            [soul-talk.pages.post :as post]
            [soul-talk.pages.user :as user]
            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [soul-talk.ajax :refer [load-interceptors!]])
  (:import goog.history.Html5History))

(defonce navs (r/atom []))
(defonce navs- (r/atom []))
(defonce main-fields (r/atom nil))
(defonce table-data (r/atom []))

(defn user-menu []
  (fn []
    (if (not= js/identity "")
      [:ul.nav.navbar-nav
       [:li.nav-item.text-nowrap.dropdown
        [:a.nav-link.dropdown-toggle
         {:href          "#"
          :id            "usermenu"
          :data-toggle   "dropdown"
          :role          "button"
          :aria-haspopup true
          :aria-expanded false}
         [:i.fa.fa-user]
         " " js/identity]
        [:div.dropdown-menu {:aria-labelledby "usermenu"}
         [:a.dropdown-item {:href "#"} "用户管理"]
         [:a.dropdown-item
          {:href "#/change-pass"}
          "密码修改"]
         [:div.dropdown-divider]
         [login/logout-button]]]]
      [:ul.navbar-nav.flex-row.ml-md-auto.d-none.d-md-flex
       [:li.nav-item
        [login/login-button]]
       [:li.nav-item
        [reg/reg-button]]])))

(defn nav-component []
  (fn []
    [:nav.navbar.navbar-dark.fixed-top.bg-dark.flex-md-nowrap.p-0.shadow
     [:a.navbar-brand.mr-0.mr-md-2
      {:href "/" :target "_blank"} "Soul Talk"]
     [user-menu]]))

(defn nav-list-component [navs]
  [:ul.nav.flex-column
   (for [{:keys [id name href icons] :as nav} navs]
     ^{:key nav} [:li#sidebarNav.nav-item
                  [:a.nav-link {:id id
                                :href href
                                :class (:current? nav)}
                   [:i {:aria-hidden true
                        :class icons}]
                   " " name
                   (when (:current? nav)
                     [:span.sr-only "(current)"])]])])

(defn sidebar-component []
  (fn []
    [:nav.col-md-2.d-none.d-md-block.bg-light.sidebar
     [:div.sidebar-sticky
      [nav-list-component @navs]
      [:h6.sidebar-heading.d-flex.justify-content-between.align-items-center.px-3.mt-4.mb-1.text-muted
       [:span "blog"]
       [:a.d-flex.align-items-center.text-muted
        {:href "#"}
        [:span {:class "plus-circle"}]]]
      [nav-list-component @navs-]]]))

(defn table-component [data]
  (fn []
    [:div
     [:h2 "Section title"]
     [:div.table-responsive
      [:table.table.table-striped.table-sm
       [:thead
        [:tr
         [:th "#"]
         [:th "Header"]
         [:th "Header"]
         [:th "Header"]
         [:th "Header"]]]
       [:tbody
        (for [{:keys [title time author public] :as d} data]
          ^{:key d} [:tr
                     [:td title]
                     [:td time]
                     [:td author]
                     [:td public]])]]]]))

(defn main-component []
  (fn []
    [:div
     [table-component @table-data]]))

(defn fluid-component []
  (fn []
    [:div.container-fluid
     [:div.row
      [sidebar-component]
      [:main#main.col-md-9.ml-sm-auto.col-lg-10.px-4 {:role "main"}
       @main-fields]]]))

(defn dash-component []
  [:div
   [nav-component]
   (if (not= js/identity "")
     [fluid-component]
     [:div#user
      [login/login-component]
      [reg/register-component]])])

(reset! navs [{:id       "home"
               :href     "#/"
               :name     "Dashboard"
               :icons    "fa fa-home fa-fw"
               :current? "active"}
              {:id    "posts"
               :href  "#/posts"
               :name  "Posts"
               :icons "fa fa-cog fa-fw"}])

(reset! table-data [{:title "title1"
                     :time "2018"
                     :author "soul"
                     :public "是"}])

(reset! main-fields
        [main-component])

(defn dash-page []
  [dash-component])