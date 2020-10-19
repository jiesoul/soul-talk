(ns soul-talk.user.handler
  (:require [re-frame.core :refer [reg-event-fx reg-event-db subscribe]]
            [ajax.core :refer [GET POST PUT]]
            [soul-talk.db :refer [api-uri]]))

(reg-event-db
  :admin/set-users
  (fn [db [_ {:keys [users]}]]
    (assoc db :admin/users users)))

(reg-event-fx
  :admin/load-users
  (fn [_ _]
    {:http {:method        GET
            :url           (str api-uri "/users")
            :success-event [:admin/set-users]}}))

(reg-event-fx
  :change-pass-ok
  (fn [{:keys [db]} [_ {:keys [user]}]]
    {:dispatch-n (list [:set-success "修改密码成功"])}))

(reg-event-fx
  :change-pass-error
  (fn [_ [_ {:keys [response]}]]
    {:dispatch [:set-error (:message response)]}))

(reg-event-fx
  :change-pass
  [reagent.debug/tracking]
  (fn [_ [_ {:keys [id email pass-old pass-new pass-confirm] :as params}]]
    {:http {:method        POST
            :url           (str api-uri "/user/" id "/password")
            :ajax-map      {:params {:email        email
                                     :pass-old     pass-old
                                     :pass-new     pass-new
                                     :pass-confirm pass-confirm}}
            :success-event [:change-pass-ok]
            :error-event   [:change-pass-error]}}))

(reg-event-fx
  :save-user-profile-ok
  (fn [_ _]
    {:dispatch-n (list [:set-success "Save User Profile Successful"])}))

(reg-event-fx
  :save-user-profile-error
  (fn [_ [_ {{message :message} :response}]]
    {:dispatch [:set-error message]}))

(reg-event-fx
  :save-user-profile
  (fn [_ [_ {:keys [id email name] :as user}]]
    {:http {:method        PUT
            :url           (str api-uri "/users/" id "/profile")
            :ajax-map      {:params user}
            :success-event [:save-user-profile-ok]
            :error-event   [:save-user-profile-error]}}))