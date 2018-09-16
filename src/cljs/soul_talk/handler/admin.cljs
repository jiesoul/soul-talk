(ns soul-talk.handler.admin
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
            [ajax.core :refer [POST]]
            [soul-talk.auth-validate :refer [login-errors]]
            [soul-talk.db :as db]
            [taoensso.timbre :as log]))

;; 运行 login
(reg-event-fx
  :run-login-events
  (fn [{{events :login-events :as db} :db} _]
    (log/info "login-events: " events)
    {:dispatch-n events
     :db db}))

;; 添加login
(reg-event-db
  :add-login-event
  (fn [db [_ event]]
    (update db :login-events conj event)
    (log/info "status:" db)))

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
  (fn [{:keys [db]} [_ {:keys [user]}]]
    {:dispatch-n (list
                       [:set-active-page :admin])
     :db         (assoc db :user user)}))

;; 处理 login error
(reg-event-fx
  :handle-login-error
  (fn [_ [_ {:keys [response] :as error}]]
    (log/error error)
    {:dispatch [:set-error (:message response)]}))

(reg-event-fx
  :logout
  (fn [_ _]
    (do
      (log/info "user log out")
      {:http {:method               POST
              :url                  "/api/logout"
              :ignore-response-body true
              :success-event        [:handle-logout]
              :error-event          [:handle-logout]}
       :db db/default-db
       :set-user! nil})))


(reg-event-fx
  :handle-logout
  (fn [_ _]
    (log/info "----------logout-------------")
    {:dispatch [:admin]}))


(reg-event-fx
  :load-dashboard
  (fn [_ _]
    {}))

