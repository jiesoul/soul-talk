(ns soul-talk.user.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db subscribe reg-fx]]
            [ajax.core :refer [GET POST PUT]]
            [soul-talk.db :refer [site-uri]]
            [soul-talk.common.local-storage :as storage]))

(reg-fx
  :set-user!
  (fn [user-identity]
    (storage/set-item! storage/login-user-key user-identity)))

(reg-fx
  :clean-user!
  (fn []
    (storage/remove-item! storage/login-user-key)))

(reg-event-db
  :users/load-all-ok
  (fn [db [_ {:keys [users]}]]
    (assoc db :users users)))

(reg-event-fx
  :users/load-all
  (fn [_ _]
    {:http {:method        GET
            :url           (str site-uri "/users")
            :success-event [:users/load-all-ok]
            :error-event   [:set-error "保存用户失败"]}}))

(reg-event-fx
  :users/password
  [reagent.debug/tracking]
  (fn [_ [_ {:keys [id email pass-old pass-new pass-confirm] :as params}]]
    {:http {:method        POST
            :url           (str site-uri "/user/" id "/password")
            :ajax-map      {:params {:email        email
                                     :pass-old     pass-old
                                     :pass-new     pass-new
                                     :pass-confirm pass-confirm}}
            :success-event [:set-success "保存用户成功"]
            :error-event   [:set-error]}}))

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
            :url           (str site-uri "/users/" id "/profile")
            :ajax-map      {:params user}
            :success-event [:save-user-profile-ok]
            :error-event   [:save-user-profile-error]}}))