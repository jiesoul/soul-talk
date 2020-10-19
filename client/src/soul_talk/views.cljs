(ns soul-talk.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [soul-talk.routes :refer [logged-in? navigate!]]
            [soul-talk.common.common :as c]
            [soul-talk.dash.page :as dash]
            [soul-talk.home.page :as home]
            [soul-talk.auth.component :as auth]
            [soul-talk.user.component :as users]
            [soul-talk.article.component :as article]
            [soul-talk.blog.page :as blog]
            [soul-talk.tag.component :as tag]
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
      (navigate! "#/dash"))))

;;后台页面
(defmethod pages :dash [_ _]
  (admin dash/dash-page))

(defmethod pages :change-pass [_ _]
  (admin users/change-pass-page))

(defmethod pages :user-profile [_ _]
  (admin users/user-profile-page))

(defmethod pages :users [_ _]
  (admin users/users-page))

(defmethod pages :articles [_ _]
  (admin article/articles-page))

(defmethod pages :articles/add [_ _]
  (admin article/add-article-page))

(defmethod pages :articles/edit [_ _]
  (admin article/edit-article-page))

(defmethod pages :default [_ _] [:div "页面未找到"])

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