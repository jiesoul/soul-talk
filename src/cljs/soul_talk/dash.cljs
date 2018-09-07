(ns soul-talk.dash
  (:require [reagent.core :as r]
            [domina :as dom]))

(defonce navs (r/atom []))

(defn nav-component []
  (fn []
    [:nav.navbar.navbar-dark.fixed-top.bg-dark.flex-md-nowrap.p-0.shadow
     [:a.navbar-brand.col-sm-3.col-md-2.mr-0
      {:href "#"} "Soul Talk"]
     [:input.form-control.form-control-dark.w-100
      {:type        :text
       :placeholder "Search"
       :aria-label  "Search"}]
     [:ul.navbar-nav.px-3
      [:li.nav-item.text-nowrap
       [:a.nav-link {:href "#"} "Sing out"]]]]))

(defn nav-list-component [navs]
  (for [{:keys [name href data-feather] :as nav} navs]
    ^{:key nav} [:li.nav-item
                 [:a.nav-link {:href href}
                  [:span {:data-feather data-feather}]
                  name]]))

(defn sidebar-component []
  (fn []
    [:nav.col-md-2.d-none.d-md-block.bg-light.sidebar
     [:div.sidebar-sticky
      [:ul.nav.flex-column
       [:li.nav-item
        [:a.nav-link.active {:href "#"}
         [:span {:data-feather "home"}]
         "Dashboard"
         [:span.sr-only "(current)"]]]
       [nav-list-component @navs]]]]))

(defn fluid-component []
  (fn []
    [:div.container-fluid
     [:div.row
      [sidebar-component]]]))


(defn dash-component []
  [:div
   [nav-component]
   [fluid-component]])

(reset! navs [{:href         "#"
                  :name         "Orders"
                  :data-feather "file"}
                 {:href         "#"
                  :name         "Products"
                  :data-feather "shopping-cat"}])

(defn ^:export init []
  (if (and js/document
           (.-getElementById js/document))
    (r/render dash-component
              (dom/by-id "app"))))