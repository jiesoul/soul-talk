(ns soul-talk.handlers
  (:require [re-frame.core :refer [inject-cofx dispatch dispatch-sync reg-event-db reg-event-fx subscribe]]
            [soul-talk.db :refer [default-db]]
            [soul-talk.base.local-storage :as storage]
            [soul-talk.article.handler]
            [soul-talk.common.errors]
            [soul-talk.auth.handler]
            [soul-talk.tag.handler]
            [soul-talk.user.handler]))

;; 初始化
(reg-event-fx
  :initialize-db
  [(inject-cofx :local-store storage/login-user-key)
   (inject-cofx :local-store storage/auth-token-key)]
  (fn [cofx _]
    (let [user (get-in cofx [:local-store storage/login-user-key])
          auth-token (get-in cofx [:local-store storage/auth-token-key])
          db (:db cofx)]
      {:db (merge db (assoc default-db :user (js->clj user :keywordize-keys true)
                                       :auth-token auth-token))})))

;; 设置当前页
(reg-event-db
  :set-active-page
  (fn [db [_ page]]
    (assoc db :active-page page)))

(reg-event-db
  :set-breadcrumb
  (fn [db [_ breadcrumb]]
    (assoc db :breadcrumb breadcrumb)))

(reg-event-fx
  :navigate-to
  (fn [_ [_ url]]
    {:navigate url}))

(reg-event-db
  :set-success
  (fn [db [_ message]]
    (assoc db :success message)))

(reg-event-db
  :clean-success
  (fn [db _]
    (dissoc db :success)))

(reg-event-db
  :update-value
  (fn [db [_ keys val]]
    (assoc-in db keys val)))

;; 取消加载
(reg-event-db
  :unset-loading
  (fn [db _]
    (dissoc db :loading? :error :should-be-loading?)))

;; 设置加载为 true
(reg-event-db
  :set-loading-for-real-this-time
  (fn [{:keys [should-be-loading?] :as db} _]
    (if should-be-loading?
      (assoc db :loading? true)
      db)))

;; 设置加载
(reg-event-fx
  :set-loading
  (fn [{db :db} _]
    {:dispatch-later [{:ms 100 :dispatch [:set-loading-for-real-this-time]}]
     :db             (-> db
                       (assoc :should-be-loading? true)
                       (dissoc :error))}))