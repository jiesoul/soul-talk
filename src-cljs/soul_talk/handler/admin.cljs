(ns soul-talk.handler.admin
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
            [ajax.core :refer [POST GET]]
            [soul-talk.auth-validate :refer [login-errors]]))

(reg-event-db
  :set-dashboard
  (fn [db {:keys [message]}]
    (assoc db :dashboard message)))

(reg-event-fx
  :load-dashboard
  (fn [_ _]
    {:http {:method GET
            :url "/api/admin/dashboard"
            :success-event [:set-dashboard]}}))
