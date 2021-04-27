(ns soul-talk.views
  (:require [re-frame.core :refer [subscribe]]
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
            [soul-talk.series.views :as series]))

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
(defmethod pages :data-dic [_ _]
  (admin data-dic/home))

(defmethod pages :data-dic/new [_ _]
  (admin data-dic/new))

(defmethod pages :data-dic/edit [_ _]
  (admin data-dic/edit))
;; 菜单
(defmethod pages :menu [_ _]
  (admin menu/home))

(defmethod pages :menu/new [_ _]
  (admin menu/new))

(defmethod pages :menu/edit [_ _]
  (admin menu/edit))
;; 角色
(defmethod pages :role [_ _]
  (admin role/home))

(defmethod pages :role/new [_ _]
  (admin role/new))

(defmethod pages :role/edit [_ _]
  (admin role/edit))

;; user
(defmethod pages :user [_ _]
  (admin users/home))

(defmethod pages :user/new [_ _]
  (admin users/new))

(defmethod pages :user/edit [_ _]
  (admin users/edit))

(defmethod pages :user/password [_ _]
  (admin users/password))

(defmethod pages :user/profile [_ _]
  (admin users/profile))

(defmethod pages :user/auth-key [_ _]
  (admin users/auth-key-home))

(defmethod pages :app-key [_ _]
  (admin app-key/home))

(defmethod pages :app-key/new [_ _]
  (admin app-key/new))

(defmethod pages :app-key/edit [_ _]
  (admin app-key/edit))

;; tag
(defmethod pages :tag [_ _]
  (admin tag/home))

(defmethod pages :tag/new [_ _]
  (admin tag/new))

(defmethod pages :tag/edit [_ _]
  (admin tag/edit))
;; 应用授权
(defmethod pages :app-key [_ _]
  (admin app-key/home))

(defmethod pages :app-key/new [_ _]
  (admin app-key/new))

(defmethod pages :app-key/edit [_ _]
  (admin app-key/edit))
;; 系列
(defmethod pages :series [_ _]
  (admin series/home))

(defmethod pages :series/new [_ _]
  (admin series/new))

(defmethod pages :series/edit [_ _]
  (admin series/edit))

;; article
(defmethod pages :article [_ _]
  (admin article/home))

(defmethod pages :article/new [_ _]
  (admin article/new))

(defmethod pages :article/edit [_ _]
  (admin article/edit))

(defmethod pages :article/view [_ _]
  (admin article/view))


;; default
(defmethod pages :default [_ _] (admin dash/home))

;; 根据配置加载不同页面
(defn main-page []
  (let [ready? (subscribe [:initialised?])
        active-page (subscribe [:active-page])]
    (when @ready?
      [:<>
       [c/lading]
       [c/success-portal]
       [c/error-portal]
       [c/confirm]
       (pages @active-page)])))