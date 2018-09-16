(ns soul-talk.handlers
  (:require [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx]]
            [soul-talk.db :as db]
            [clojure.string :as str]
            [ajax.core :refer [POST]]
            [soul-talk.auth-validate :refer [login-errors]]
            soul-talk.handler.errors
            soul-talk.handler.admin
            [taoensso.timbre :as log]))

;; 初始化默认的事件
(reg-event-db
  :initialize-db
  (fn [_ _]
    (log/info "init db:" db/default-db)
    db/default-db))

;; 设置当前页
(reg-event-db
  :set-active-page
  (fn [db [_ page]]
    (assoc db :active-page page)))

(reg-event-fx
  :navigate-to
  (fn [_ [_ url]]
    {:navigate url}))

;; 设置加载为 true
(reg-event-db
  :set-loading-for-real-this-time
  (fn [{:keys [should-be-loading?] :as db} _]
    (if should-be-loading?
      (assoc db :loading true)
      db)))

;; 设置加载
(reg-event-fx
  :set-loading
  (fn [{db :db} _]
    {:dispatch-later [{:ms 100 :dispatch [:set-loading-for-real-this-time]}]
     :db (-> db
             (assoc :should-be-loading? true)
             (dissoc :error))}))

;; 取消加载
(reg-event-db
  :unset-loading
  (fn [db _]
    (dissoc db :loading? :error :should-be-loading?)))