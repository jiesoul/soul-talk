(ns soul-talk.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [soul-talk.routes :refer [logged-in? navigate!]]
            [soul-talk.common.styles :as styles]
            [soul-talk.common.views :as c]
            [soul-talk.dash.views :as dash]
            [soul-talk.site-info.views :as site-info]
            [soul-talk.auth.views :as auth]
            [soul-talk.user.views :as users]
            [soul-talk.article.views :as article]
            [soul-talk.tag.views :as tag]
            [soul-talk.app-key.views :as app-key]
            [soul-talk.data-dic.views :as data-dic]
            [soul-talk.menu.views :as menu]
            [soul-talk.role.views :as role]
            [soul-talk.collect-link.views :as collect-link]
            [soul-talk.collect-site.views :as collect-site]
            [soul-talk.series.views :as series]
            ["@material-ui/core" :as mui]
            ["@material-ui/core/styles" :refer [ThemeProvider]]
            ["react-toastify" :refer [ToastContainer]]
            [soul-talk.utils :as utils]))

;;多重方法  响应对应的页面
(defmulti pages (fn [page _] page))

;;页面
(defmethod pages :login [_ _] [auth/login-page])

(defn admin [page]
  (let [user (subscribe [:user])]
    (if @user
      [page]
      [auth/login-page])))

;;面板
(defmethod pages :dash [_ _]
  (admin dash/home))

(defmethod pages :site-info [_ _]
  (admin site-info/home))

(defmethod pages :data-dices [_ _]
  (admin data-dic/home))

(defmethod pages :menus [_ _]
  (admin menu/home))

(defmethod pages :menus/add [_ _]
  (admin menu/add))

(defmethod pages :menus/edit [_ _]
  (admin menu/edit))

(defmethod pages :roles [_ _]
  (admin role/home))

(defmethod pages :roles/add [_ _]
  (admin role/add))

(defmethod pages :roles/edit [_ _]
  (admin role/edit))


;; user
(defmethod pages :users [_ _]
  (admin users/home))

(defmethod pages :users-password [_ _]
  (admin users/change-pass))

(defmethod pages :users-profile [_ _]
  (admin users/user-profile))

;; tag
(defmethod pages :tags [_ _]
  (admin tag/home))

(defmethod pages :app-keys [_ _]
  (admin app-key/home))

(defmethod pages :series [_ _]
  (admin series/home))

;; article
(defmethod pages :articles [_ _]
  (admin article/home))

(defmethod pages :articles/add [_ _]
  (admin article/add))

(defmethod pages :articles/edit [_ _]
  (admin article/edit))

(defmethod pages :articles/view [_ _]
  (admin article/edit))


;; default
(defmethod pages :default [_ _] (admin dash/home))

;; 根据配置加载不同页面
(defn main-page []
  (let [ready? (subscribe [:initialised?])
        active-page (subscribe [:active-page])]
    (when @ready?
      (fn []
        [styles/theme-provider styles/custom-theme
         [:<>
          [c/success]
          [c/error]
          [:> ToastContainer {:auto-close 3000
                              :newest-on-top true}]
          [c/lading]
          (pages @active-page)]]))))