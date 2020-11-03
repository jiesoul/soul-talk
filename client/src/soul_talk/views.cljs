(ns soul-talk.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [soul-talk.routes :refer [logged-in? navigate!]]
            [soul-talk.common.views :as c]
            [soul-talk.dash.views :as dash]
            [soul-talk.home.view :as home]
            [soul-talk.auth.views :as auth]
            [soul-talk.user.views :as users]
            [soul-talk.article.views :as article]
            [soul-talk.blog.views :as blog]
            [soul-talk.tag.views :as tag]
            [clojure.string :as str]))

;;多重方法  响应对应的页面
(defmulti pages (fn [page _] page))

;;页面
(defmethod pages :home [_ _] [home/home-page])
(defmethod pages :login [_ _] [auth/login-page])
(defmethod pages :register [_ _] [auth/register-page])
(defmethod pages :blog/archives [_ _] [blog/blog-archives-page])
(defmethod pages :blog [_ _] [blog/blog-page])
(defmethod pages :articles/view [_ _] [article/article-view-page])

(defn admin [page]
  (r/with-let [user (subscribe [:user])]
    (if @user
      [page]
      (navigate! "#/login"))))

;;后台页面
(defmethod pages :dash [_ _]
  (admin dash/dash-page))

(defmethod pages :users-password [_ _]
  (admin users/change-pass-page))

(defmethod pages :users-profile [_ _]
  (admin users/user-profile-page))

(defmethod pages :users [_ _]
  (admin users/users-page))

(defmethod pages :tags [_ _]
  (admin tag/tags-page))

(defmethod pages :articles [_ _]
  (admin article/articles-page))

(defmethod pages :articles-add [_ _]
  (admin article/add-article-page))

(defmethod pages :articles-edit [_ _]
  (admin article/edit-article-page))

(defmethod pages :default [_ _] [:div "页面未找到,请检查URL"])

;; 根据配置加载不同页面
(defn main-page []
  (r/with-let [ready? (subscribe [:initialised?])
               active-page (subscribe [:active-page])]
    (when @ready?
      (fn []
        [:div
         [c/success-modal]
         [c/error-modal]
         (pages @active-page nil)]))))