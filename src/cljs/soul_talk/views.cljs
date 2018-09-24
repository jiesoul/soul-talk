(ns soul-talk.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [soul-talk.routes :refer [logged-in?]]
            [soul-talk.pages.home :refer [home-page]]
            [soul-talk.pages.admin :refer [main-component]]
            [soul-talk.pages.auth :refer [login-page register-page]]
            [soul-talk.pages.users :refer [users-page change-pass-page user-profile-page]]
            [soul-talk.pages.post :refer [posts-page create-post-page post-view-page]]
            [soul-talk.pages.category :as category]
            [soul-talk.pages.tag :as tag]
            [taoensso.timbre :as log]
            [clojure.string :as str]))

(defn admin-user-menu [user]
  (if @user
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
       " " (if (str/blank? (:name @user)) (:email @user) (:name @user))]
      [:div.dropdown-menu.dropdown-menu-right
       {:aria-labelledby "usermenu"}
       [:a.dropdown-item {:href "/user-profile"} "Your Profile"]
       [:a.dropdown-item
        {:href "/change-pass"}
        "密码修改"]
       [:div.dropdown-divider]
       [:a.dropdown-item.btn
        {:on-click #(dispatch [:logout])}
        "退出"]]]]))

(defn admin-navbar [user]
  (fn []
    [:nav.navbar.navbar-dark.fixed-top.bg-dark.flex-md-nowrap.p-0.shadow
     [:a.navbar-brand.mr-0.mr-md-2
      {:href "/" :target "_blank"} "Soul Talk"]
     [admin-user-menu user]]))


(defn admin-sidebar-link [url title page]
  (let [active-page (subscribe [:active-page])]
    [:li.nav-item
     [:a.nav-link
      {:href  url
       :class (when (= page @active-page)
                "active")}
      title]]))

(defn admin-sidebar []
  (fn []
    (r/with-let [user (subscribe [:user])]
      (when @user
        [:nav.col-md-2.d-none.d-md-block.bg-light.sidebar
         [:div.sidebar-sticky
          [:ul.nav.flex-column
           (admin-sidebar-link "/admin" "Dashboard" :admin)
           (admin-sidebar-link "/categories" "Categories" :categories)
           (admin-sidebar-link "/posts" "Posts" :posts)
           (admin-sidebar-link "/users" "Users" :users)]]]))))


;;多重方法  响应对应的页面
(defmulti pages (fn [page _] page))

(defn admin-page [main]
  (r/with-let
    [user (subscribe [:user])]
    (if @user
      [:div.container-fluid
       [admin-navbar user]
       [:div.container-fluid
        [:div.row
         [admin-sidebar]
         [:main#main.col-md-9.ml-sm-auto.col-lg-10.px-4 {:role "main"}
          [main]]]]]
      (pages :login nil))))

;;页面
(defmethod pages :home [_ _] [home-page])
(defmethod pages :login [_ _] [login-page])
(defmethod pages :register [_ _] [register-page])

;;后台页面
(defmethod pages :admin [_ _]
  (admin-page main-component))

(defmethod pages :change-pass [_ _]
  (admin-page change-pass-page))

(defmethod pages :user-profile [_ _]
  (admin-page user-profile-page))

(defmethod pages :users [_ _]
  (admin-page users-page))

(defmethod pages :categories [_ _]
  (admin-page category/categories-page))

(defmethod pages :categories/add [_ _]
  (admin-page category/add-page))

(defmethod pages :posts [_ _]
  (admin-page posts-page))

(defmethod pages :posts/add [_ _]
  (r/with-let [user (subscribe [:user])]
              (if @user
                [:div.container-fluid
                 [:nav.navbar.navbar-expand-lg.navbar-light.bg-light
                  [:a.navbar-brand
                   {:href "#"} "Soul Talk"]
                  [:div.container
                   [:ul.navbar-nav
                    [:li.nav-item.active
                     [:a.nav-link
                      {:href "#"}
                      "写文章"]]]]]
                 [:div.container
                  [create-post-page]]]
                (pages :login nil))))


(defmethod pages :posts/view [_ _]
  (.scrollTo js/window 0 0)
  [post-view-page])

(defmethod pages :tags/add [_ _]
  (admin-page tag/add-page))

(defmethod pages :default [_ _] [:div "default show ......"])

;; 根据配置加载不同页面
(defn main-page []
  (r/with-let
    [active-page (subscribe [:active-page])]
     (pages @active-page)))