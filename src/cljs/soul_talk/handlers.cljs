(ns soul-talk.handlers
  (:require [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx subscribe]]
            [soul-talk.db :as db]
            [ajax.core :refer [POST]]
            [soul-talk.auth-validate :refer [login-errors reg-errors]]
            soul-talk.handler.errors
            soul-talk.handler.admin
            soul-talk.handler.users
            soul-talk.handler.posts
            soul-talk.handler.category
            soul-talk.handler.tag
            soul-talk.handler.files))

;; 初始化
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

;; 运行 login
(reg-event-fx
  :run-login-events
  (fn [{{events :login-events :as db} :db} _]
    {:dispatch-n events
     :db db}))

;; 添加login
(reg-event-db
  :add-login-event
  (fn [db [_ event]]
    (update db :login-events conj event)))

;; 处理login ok
(reg-event-fx
  :handle-login
  (fn [{:keys [db]} [_ {:keys [user]}]]
    {:dispatch-n (list
                   [:navigate-to "/admin"])
     :db         (assoc db :user user)}))

;; login
(reg-event-fx
  :login
  (fn [_ [_ {:keys [email password] :as user}]]
    (if-let [error (login-errors user)]
      {:dispatch [:set-error (first error)]}
      {:http {:method POST
              :url "/api/login"
              :ajax-map {:params {:email email
                                  :password password}}
              :success-event [:handle-login]}})))


;; 处理register ok
(reg-event-fx
  :handle-register
  (fn [{:keys [db]} [_ {:keys [user]}]]
    (js/alert "register ok")
    {:dispatch-n (list
                   [:set-active-page :admin])
     :db         (assoc db :user user)}))

;; register
(reg-event-fx
  :register
  (fn [_ [_ {:keys [email password pass-confirm] :as user}]]
    (if-let [error (reg-errors user)]
      {:dispatch [:set-error (first error)]}
      {:http {:method POST
              :url "/api/register"
              :ajax-map {:params {:email email
                                  :password password
                                  :pass-confirm pass-confirm}}
              :success-event [:handle-register]}})))

(reg-event-fx
  :handle-logout
  (fn [_ _]
    {:reload-page true}))

(reg-event-fx
  :logout
  (fn [_ _]
    {:http      {:method               POST
                 :url                  "/api/logout"
                 :ignore-response-body true
                 :success-event        [:handle-logout]
                 :error-event          [:handle-logout]}
     :db        db/default-db
     :set-user! nil}))


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