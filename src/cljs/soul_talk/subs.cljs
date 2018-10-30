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

(reg-sub :error query)

(reg-sub :login-events query)

(reg-sub :admin/users query)

(reg-sub :loading? query)

(reg-sub :admin/posts query)

(reg-sub :categories query)

(reg-sub :tags query)

(reg-sub :post query)

(reg-sub :posts query)

(reg-sub :posts-archives query)

(reg-sub :pagination query)

(reg-sub :admin/pagination query)

(reg-sub :upload/md query)
