(ns soul-talk.subs
  (:require [re-frame.core :refer [reg-sub]]))

;; 获取当时全部数据
(reg-sub
  :db-state
  (fn [db _]
    db))

;; 响应事件
(defn query [db [event-id]]
  (event-id db))

;; 当前页配置
(reg-sub :active-page query)

(reg-sub :user query)

(reg-sub :loading? query)

(reg-sub :login-events query)

(reg-sub :error query)
