(ns soul-talk.auth.events
  (:require [soul-talk.db :refer [api-uri] :as db]
            [re-frame.core :refer [reg-event-fx reg-event-db dispatch inject-cofx reg-fx]]
            [ajax.core :refer [POST GET DELETE PUT]]
            [soul-talk.common.local-storage :refer [login-user-key auth-token-key]]
            [soul-talk.common.local-storage :as storage]))

(reg-fx
  :set-login-token!
  (fn [auth-token]
    (storage/set-item! storage/auth-token-key auth-token)))

(reg-fx
  :clean-login-token
  (fn []
    (storage/remove-item! storage/auth-token-key)))

;; 处理login ok
(reg-event-fx
  :login-ok
  (fn [{:keys [db]} [_ {:keys [user token]}]]
    (let []
      {:db               (assoc db :user user :login-token token)
       :dispatch-n       (list
                           [:run-login-events]
                           [:navigate-to "/#/dash"])
       :set-user!        user
       :set-login-token! (str "Token " token)})))

(reg-event-db
  :login-error
  (fn [db {:keys [error]}]
    (assoc db :error error)))

;; login
(reg-event-fx
  :login
  (fn [_ [_ login-user]]
    {:http {:method        POST
            :url           (str api-uri "/login")
            :ajax-map      {:params login-user}
            :success-event [:login-ok]
            :error-event [:login-error]}}))


(reg-event-fx
  :logout-ok
  (fn [_ _]
    {:dispatch-n       (list
                         [:set-active-page :login])
     :set-user!        nil
     :set-login-token! nil
     :db               db/default-db}))

(reg-event-fx
  :logout
  (fn [_ {:keys [id]}]
    {:http {:method               POST
            :url                  (str api-uri "/logout")
            :ignore-response-body true
            :ajax-map {:params {:user_id id}}
            :success-event        [:handle-logout-ok]}}))

;;
(reg-event-db
  :add-login-event
  (fn [db [_ event]]
    (update db :login-events conj event)))

;;
(reg-event-fx
  :run-login-events
  (fn [{{events :login-events :as db} :db} _]
    {:dispatch-n events
     :db         db}))




;;; 处理register ok
;(reg-event-fx
;  :handle-register
;  (fn [{:keys [db]} [_ {:keys [user]}]]
;    {:dispatch-n (list
;                   [:set-active-page :admin])
;     :db         (assoc db :user user)}))
;
;;; register
;(reg-event-fx
;  :register
;  (fn [_ [_ {:keys [email password pass-confirm] :as user}]]
;    {:http {:method        POST
;            :url           (str api-uri "/register")
;            :ajax-map      {:params {:email        email
;                                     :password     password
;                                     :pass-confirm pass-confirm}}
;            :success-event [:handle-register]}}))




