(ns soul-talk.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [soul-talk.pages.home :refer [home-page]]
            [soul-talk.pages.admin :refer [admin-page]]
            [soul-talk.pages.auth :refer [login-page]]
            [soul-talk.pages.register :refer [register-component]]
            [taoensso.timbre :as log]))

;;多重方法  响应对应的页面
(defmulti pages (fn [page _] page))

(defmethod pages :home [_ _] [home-page])
(defmethod pages :admin [_ user]
  (log/info "-------------" (js->clj js/user :keywords? true))
  (if (nil? (js->clj js/user))
    (pages :login nil)
    [admin-page]))
(defmethod pages :login [_ _] [login-page])
;(defmethod pages :register [_ _] [register-component])
(defmethod pages :default [_ _] [:div "default show ......"])

;; 根据配置加载不同页面
(defn main-page []
  (log/info "app-db:" @(subscribe [:db-state]))
  (r/with-let [active-page (subscribe [:active-page])
                user (subscribe [:user])]
     (pages @active-page @user)))