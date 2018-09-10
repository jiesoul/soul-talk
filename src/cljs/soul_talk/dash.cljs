(ns soul-talk.dash
  (:require [reagent.core :as r]
            [domina :as dom]
            [soul-talk.components.common :as c]
            [soul-talk.login :as login]
            [cljsjs.chartjs]
            [reagent.session :as session]
            [soul-talk.post :as post]
            [soul-talk.user :as user]))

(defonce navs (r/atom []))
(defonce navs- (r/atom []))
(defonce main-fields (r/atom nil))
(defonce table-data (r/atom []))

(defn user-menu []
  (fn []
    (if (not= js/identity "")
      [:ul.navbar-nav.px-3.mr-auto
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
          {:href "#change-pass"
           :on-click #(reset! main-fields [user/change-pass-form])}
          "密码修改"]
         [:div.dropdown-divider]
         [:a.dropdown-item {:href "/logout"} "退出"]]]])))

(defn nav-component []
  (fn []
    [:nav.navbar.navbar-dark.fixed-top.bg-dark.flex-md-nowrap.p-0.shadow
     [:a.navbar-brand.col-sm-3.col-md-2.mr-0
      {:href "/" :target "_blank"} "Soul Talk"]
     [:input.form-control.form-control-dark.w-100
      {:type        :text
       :placeholder "Search"
       :aria-label  "Search"}]
     [user-menu]]))

(defn nav-list-component [navs]
  [:ul.nav.flex-column
   (for [{:keys [name href icons fun] :as nav} navs]
     ^{:key nav} [:li.nav-item
                  [:a.nav-link {:href href
                                :class (:current? nav)
                                :on-click fun}
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

(defn dashboard-component []
  (fn []
    [:div.d-flex.justify-content-between.flex-wrap.flex-md-nowrap.align-items-center.pt-3.pb-2.mb-3.border-bottom
     [:h1.h2 "Dashboard"]
     [:div.btn-toolbar.mb-2.mb-md-0
      [:div.btn-group.mr-2
       [:button.btn.btn-sm.btn-outline-secondary "Share"]
       [:button.btn.btn-sm.btn-outline-secondary "Export"]
       ]]]))

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
    [:div
     [dashboard-component]
     [canvas-component]
     [table-component @table-data]]))

(defn fluid-component []
  (fn []
    [:div.container-fluid
     [:div.row
      [sidebar-component]
      [:main.col-md-9.ml-sm-auto.col-lg-10.px-4 {:role "main"}
       @main-fields]]]))

(defn dash-component []
  [:div
   [nav-component]
   (if (not= js/identity "")
     [fluid-component]
     [login/login-component])])

(reset! navs [{:href  "#board"
               :name  "Dashboard"
               :icons "fa fa-home fa-fw"
               :current? "active"
               :fun #(reset! main-fields [main-component])}
              {:href  "#posts"
               :name  "Posts"
               :icons "fa fa-cog fa-fw"
               :fun   #(do
                         (reset! main-fields [post/posts-component]))}])

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