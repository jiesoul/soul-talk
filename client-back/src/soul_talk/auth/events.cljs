(ns soul-talk.auth.events
  (:require [soul-talk.db :refer [site-uri] :as db]
            [re-frame.core :refer [reg-event-fx reg-event-db dispatch inject-cofx reg-fx]]
            [ajax.core :refer [POST GET DELETE PUT]]
            [soul-talk.common.local-storage :refer [login-user-key login-token-key]]
            [soul-talk.common.local-storage :as storage]))

(reg-fx
  :set-login-token!
  (fn [login-token]
    (storage/set-item! login-token-key login-token)))

(reg-fx
  :clean-login-token
  (fn []
    (storage/remove-item! login-token-key)))

;; 处理login ok
(reg-event-fx
  :login-ok
  (fn [{:keys [db]} [_ {:keys [user] :as resp}]]
    (let [login-events (:login-events db)]
      {:db               (assoc db :user user)
       :dispatch-n       (list
                           [:navigate-to "#/dash"]
                           [:run-login-events])
       :set-user!        user})))

(reg-event-db
  :login-error
  (fn [db {:keys [error]}]
    (assoc db :error error)))

;; login
(reg-event-fx
  :login
  (fn [_ [_ login-user]]
    {:http {:method        POST
            :url           (str site-uri "/login")
            :ajax-map      {:params login-user}
            :success-event [:login-ok]}}))


(reg-event-fx
  :logout-ok
  (fn [_ _]
    {:dispatch-n       (list
                         [:navigate-to "#/login"]
                         [:site-info/load 1])
     :set-user!        nil
     :set-login-token! nil
     :db               db/default-db}))

(reg-event-fx
  :logout
  (fn [_ {:keys [id]}]
    {:http {:method               POST
            :url                  (str site-uri "/logout")
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

(reg-event-db
  :auth-key/init
  (fn [db _]
    (-> db
      (assoc :auth-keys/delete-dialog false)
      (dissoc :auth-key/list :auth-keys/edit :auth-key/query-params :auth-key/pagination))))

(reg-event-db
  :auth-key/load-page-ok
  (fn [db [_ {:keys [app-keys pagination query-params]}]]
    (assoc db :auth-key/list app-keys :auth-key/pagination pagination :auth-key/query-params query-params)))

(reg-event-fx
  :auth-key/load-page
  (fn [_ params]
    {:http {:method        GET
            :url           (str site-uri "/app-keys")
            :ajax-map      {:params params}
            :success-event [:auth-key/load-page-ok]}}))

(reg-event-db
  :auth-key/set-delete-dialog
  (fn [db [_ value]]
    (assoc db :auth-key/delete-dialog value)))

(reg-event-db
  :auth-key/set-attr
  (fn [db [_ attr]]
    (update-in db [:auth-key/edit] merge attr)))

(reg-event-db
  :auth-key/delete-ok
  (fn [db [_ id]]
    (let [auth-keys (:auth-keys db)
          auth-keys (remove #(= id (:id %)) auth-keys)]
      (assoc db :success "删除成功" :auth-key/list auth-keys))))

(reg-event-fx
  :auth-key/delete
  (fn [_ [_ id]]
    {:http {:method  DELETE
            :url (str site-uri "/auth-keys/" id)
            :success-event [:auth-key/delete-ok id]}}))

