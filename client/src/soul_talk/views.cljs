(ns soul-talk.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [soul-talk.routes :refer [logged-in? navigate!]]
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
;; 网站信息
(defmethod pages :site-info [_ _]
  (admin site-info/edit))
;; 数据字典
(defmethod pages :data-dices [_ _]
  (admin data-dic/home))

(defmethod pages :data-dices/new [_ _]
  (admin data-dic/new))

(defmethod pages :data-dices/edit [_ _]
  (admin data-dic/edit))
;; 菜单
(defmethod pages :menus [_ _]
  (admin menu/home))

(defmethod pages :menus/new [_ _]
  (admin menu/new))

(defmethod pages :menus/edit [_ _]
  (admin menu/edit))
;; 角色
(defmethod pages :roles [_ _]
  (admin role/home))

(defmethod pages :roles/new [_ _]
  (admin role/new))

(defmethod pages :roles/edit [_ _]
  (admin role/edit))

;; user
(defmethod pages :users [_ _]
  (admin users/home))

(defmethod pages :users/new [_ _]
  (admin users/new))

(defmethod pages :users/edit [_ _]
  (admin users/edit))

(defmethod pages :users/password [_ _]
  (admin users/password))

(defmethod pages :users/profile [_ _]
  (admin users/profile))

;; tag
(defmethod pages :tags [_ _]
  (admin tag/home))

(defmethod pages :tags/new [_ _]
  (admin tag/new))

(defmethod pages :tags/new [_ _]
  (admin tag/edit))
;; 应用授权
(defmethod pages :app-keys [_ _]
  (admin app-key/home))

(defmethod pages :app-keys/new [_ _]
  (admin app-key/new))

(defmethod pages :app-keys/edit [_ _]
  (admin app-key/edit))
;; 系列
(defmethod pages :series [_ _]
  (admin series/home))

(defmethod pages :series/new [_ _]
  (admin series/new))

(defmethod pages :series/edit [_ _]
  (admin series/edit))

;; article
(defmethod pages :articles [_ _]
  (admin article/home))

(defmethod pages :articles/new [_ _]
  (admin article/new))

(defmethod pages :articles/edit [_ _]
  (admin article/edit))

(defmethod pages :articles/view [_ _]
  (admin article/view))


;; default
(defmethod pages :default [_ _] (admin dash/home))

;; 根据配置加载不同页面
(defn main-page []
  (let [ready? (subscribe [:initialised?])
        active-page (subscribe [:active-page])]
    (when @ready?
      [:<>
       [:> ToastContainer {:auto-close    3000
                           :newest-on-top true}]
       [c/lading]
       (pages @active-page)])))