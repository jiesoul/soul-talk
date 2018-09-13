(ns soul-talk.handlers
  (:require [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx]]
            [soul-talk.db :as db]))

;; 初始化默认的事件
(reg-event-db
  :initialize-db
  (fn [_ _]
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

