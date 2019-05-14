(ns soul-talk.handler.users
  (:require [re-frame.core :refer [reg-event-fx reg-event-db subscribe]]
            [ajax.core :refer [GET POST PUT]]
            [soul-talk.auth-validate :refer [change-pass-errors]]
            [taoensso.timbre :as log]))

(reg-event-db
  :admin/set-users
  (fn [db [_ {:keys [users]}]]
    (assoc db :admin/users users)))

(reg-event-fx
  :admin/load-users
  (fn [_ _]
    {:http {:method GET
            :url "/api/admin/users"
            :success-event [:admin/set-users]}}))

(reg-event-fx
  :change-pass-ok
  (fn [{:keys [db]} [_ {:keys [user]}]]
    (js/alert "Change Password successful!!")
    {:db (assoc db :user user)}))

(reg-event-fx
  :change-pass-error
  (fn [_ [_ {:keys [response]}]]
    {:dispatch [:set-error (:message response)]}))

(reg-event-fx
  :change-pass
  (fn [_ [_ {:keys [email pass-old pass-new pass-confirm] :as params}]]
    (if-let [error (change-pass-errors params)]
      {:dispatch [:set-error (first error)]}
      {:http {:method POST
              :url "/api/admin/change-pass"
              :ajax-map {:params {:email email
                                  :pass-old pass-old
                                  :pass-new pass-new
                                  :pass-confirm pass-confirm}}
              :success-event [:change-pass-ok]
              :error-event [:change-pass-error]}})))

(reg-event-fx
  :save-user-profile-ok
  (fn [_ _]
    (js/alert "Save User Profile Successful")))

(reg-event-fx
  :save-user-profile-error
  (fn [_ [_ {{message :message} :response}]]
    {:dispatch [:set-error message]}))

(reg-event-fx
  :save-user-profile
  (fn [_ [_ {:keys [email name] :as user}]]
    {:http {:method POST
            :url "/api/admin/user-profile"
            :ajax-map {:params user}
            :success-event [:save-user-profile-ok]
            :error-event [:save-user-profile-error]}}))