(ns soul-talk.auth.events
  (:require [soul-talk.db :refer [api-url] :as db]
            [re-frame.core :refer [reg-event-fx reg-event-db dispatch inject-cofx reg-fx]]
            [ajax.core :refer [POST GET DELETE PUT]]
            [soul-talk.common.local-storage :refer [set-item! remove-item! login-user-key login-token-key]]))

(reg-fx
  :set-login-user!
  (fn [user-identity]
    (set-item! login-user-key user-identity)))

(reg-fx
  :clean-user!
  (fn []
    (remove-item! login-user-key)))

;; 处理login ok
(reg-event-fx
  :login-ok
  (fn [{:keys [db]} [_ {:keys [user] :as resp}]]
    {:db         (assoc db :user user)
     :dispatch-n (list
                   [:user/load-menus (:id user)]
                   [:navigate-to "#/dash"])
     :set-login-user!  user}))

(reg-event-db
  :login-error
  (fn [db {:keys [error]}]
    (assoc db :error error)))

;; login
(reg-event-fx
  :login
  (fn [_ [_ login-user]]
    {:http {:method        POST
            :url           (str api-url "/login")
            :ajax-map      {:params login-user}
            :success-event [:login-ok]}}))


(reg-event-fx
  :logout-ok
  (fn [_ _]
    {:dispatch-n       (list
                         [:navigate-to "#/login"]
                         [:site-info/load 1])
     :set-login-user!        nil
     :db               db/default-db}))

(reg-event-fx
  :logout
  (fn [_ {:keys [id]}]
    {:http {:method               POST
            :url                  (str api-url "/logout")
            :ignore-response-body true
            :ajax-map {:params {:user_id id}}
            :success-event        [:logout-ok]}}))

(reg-event-db
  :add-login-event
  (fn [db [_ event]]
    (assoc db :login-events event)))

;;
(reg-event-fx
  :run-login-events
  (fn [{{events :login-events :as db} :db} _]
    {:dispatch-n events
     :db         (dissoc db :login-events)}))

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
;            :url           (str site-uri "/register")
;            :ajax-map      {:params {:email        email
;                                     :password     password
;                                     :pass-confirm pass-confirm}}
;            :success-event [:handle-register]}}))



