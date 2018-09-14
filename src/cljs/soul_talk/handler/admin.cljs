(ns soul-talk.handler.admin
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
            [ajax.core :refer [POST]]
            [soul-talk.auth-validate :refer [login-errors]]
            [taoensso.timbre :as log]))

;; 运行 login
(reg-event-fx
  :run-login-events
  (fn [{{events :login-events :as db} :db} _]
    (log/info events)
    {:dispatch-n events
     :db db}))

;; 添加login
(reg-event-db
  :add-login-event
  (fn [db [_ event]]
    (update db :login-events conj event)))

;; login
(reg-event-fx
  :login
  (fn [_ [_ {:keys [email password] :as user}]]
    (if-let [error (login-errors user)]
      (do
        (log/error "验证失败： " error)
        {:dispatch [:set-error (first error)]})
      {:http {:method POST
              :url "/api/login"
              :ajax-map {:params {:email email
                                  :password password}}
              :success-event [:handle-login]
              :error-event [:handle-login-error]}})))

;; 处理login ok
(reg-event-fx
  :handle-login
  (fn [{:keys [db]} [_ {:keys [user] :as resp}]]
    {:dispatch-n (list [:run-login-events]
                        [:set-active-page :admin])}
    :db (assoc db :user user)))

;; 处理 login error
(reg-event-fx
  :handle-login-error
  (fn [_ [_ {:keys [response] :as error}]]
    (log/error error)
    {:dispatch [:set-error (:message response)]}))
