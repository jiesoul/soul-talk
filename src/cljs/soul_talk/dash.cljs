(ns soul-talk.dash
  (:require [reagent.core :as r]
            [domina :as dom]
            [soul-talk.components.common :as c]
            [soul-talk.login :as login]
            [cljsjs.chartjs]
            [reagent.session :as session]))

(defonce navs (r/atom []))
(defonce navs- (r/atom []))
(defonce main-fields (r/atom nil))
(defonce table-data (r/atom []))

(defn log-component []
  (fn []
    (if (not= js/identity "")
      [:ul.:ul.navbar-nav.px-3
       [:li.nav-item.text-nowrap
        [:a.nav-link {:href "/logout"}
         [:i.fa.fa-user]
         " " js/identity " | 退出"]]
       [login/login-button]])))

(defn nav-component []
  (fn []
    [:nav.navbar.navbar-dark.fixed-top.bg-dark.flex-md-nowrap.p-0.shadow
     [:a.navbar-brand.col-sm-3.col-md-2.mr-0
      {:href "/" :target "_blank"} "Soul Talk"]
     [:input.form-control.form-control-dark.w-100
      {:type        :text
       :placeholder "Search"
       :aria-label  "Search"}]
     [log-component]]))

(defn nav-list-component [navs]
  [:ul.nav.flex-column
   (for [{:keys [name href icons] :as nav} navs]
     ^{:key nav} [:li.nav-item
                  [:a.nav-link {:href href :class (:current? nav)}
                   [:i {:aria-hidden true
                        :class icons}]
                   (str " " name)
                   (when (:current? nav)
                     [:span.sr-only "(current)"])]])])

(defn sidebar-component []
  (fn []
    [:nav.col-md-2.d-none.d-md-block.bg-light.sidebar
     [:div.sidebar-sticky
      [nav-list-component @navs]
      [:h6.sidebar-heading.d-flex.justify-content-between.align-items-center.px-3.mt-4.mb-1.text-muted
       [:span "Saved Report"]
       [:a.d-flex.align-items-center.text-muted
        {:href "#"}
        [:span {:class "plus-circle"}]]]
      [nav-list-component @navs-]]]))

(defn dashboard-component []
  (fn []
    [:div.d-flex.justify-content-between.flex-wrap.flex-md-nowrap.align-items-center.pt-3.pb-2.mb-3.border-bottom
     [:h1.h2 "Dashboard"]
     [:div.btn-toolbar.mb-2.mb-md-0
      [:div.btn-group.mr-2
       [:button.btn.btn-sm.btn-outline-secondary "Share"]
       [:button.btn.btn-sm.btn-outline-secondary "Export"]]
      [:button.btn.btn-sm.btn-outline-secondary.dropdown-toggle
       [:span.fa.fa-calendar]
       "This work"]]]))

(defn show-revenue-chart
  []
  (let [context (.getContext (dom/by-id "rev-chartjs") "2d")
        chart-data {:type "bar"
                    :data {:labels ["2012" "2013" "2014" "2015" "2016"]
                           :datasets [{:data [5 10 15 20 25]
                                       :label "Rev in MM"
                                       :backgroundColor "#90EE90"}
                                      {:data [3 6 9 12 15]
                                       :label "Cost in MM"
                                       :backgroundColor "#F08080"}]}}]
    (js/Chart. context (clj->js chart-data))))

(defn canvas-component
  []
  (r/create-class
    {:component-did-mount #(show-revenue-chart)
     :display-name        "chartjs-component"
     :reagent-render      (fn []
                            [:canvas {:id "rev-chartjs" :width "900" :height "300"}])}))

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
    [:main.col-md-9.ml-sm-auto.col-lg-10.px-4 {:role "main"}
     [dashboard-component]
     [canvas-component]
     [table-component @table-data]]))

(defn fluid-component []
  (fn []
    [:div.container-fluid
     [:div.row
      [sidebar-component]
      @main-fields]]))

(defn dash-component []
  [:div
   [nav-component]
   (if (not= js/identity "")
     [fluid-component]
     [login/login-component])])

(reset! navs [{:href  "#"
               :name  "Dashboard"
               :icons "fa fa-home fa-fw"
               :current? "active"}
              {:href  "#"
               :name  "Orders"
               :icons "fa fa-book fa-fw"}
              {:href  "#"
               :name  "Products"
               :icons "fa fa-cog fa-fw"}])

(reset! navs- [{:href  "#"
                :name  "Current month"
                :icons  "fa fa-cog fa-fw"}
               {:href  "#"
                :name  "Current year"
                :icons "fa fa-cog fa-fw"}
               {:href  "#"
                :name  "Current day"
                :icons "fa fa-cog fa-fw"}])

(reset! table-data [{:title "title1"
                     :time "2018"
                     :author "soul"
                     :public "是"}])

(reset! main-fields
        [main-component])

(defn ^:export init []
  (if (and js/document
           (.-getElementById js/document))
    (do
      (r/render dash-component
                (dom/by-id "app")))))