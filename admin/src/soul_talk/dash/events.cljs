(ns soul-talk.dash.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
            [ajax.core :refer [POST GET]]
            [soul-talk.db :refer [api-url]]))

(reg-event-db
  :set-dashboard
  (fn [db {:keys [message]}]
    (assoc db :dashboard message)))

(reg-event-fx
  :load-dashboard
  (fn [_ _]
    {:http {:method        GET
            :url           (str api-url "/dashboard")
            :success-event [:set-dashboard]}}))

(reg-event-db
  :set-breadcrumb
  (fn [db [_ breadcrumb]]
    (assoc db :breadcrumb breadcrumb)))

(reg-event-db
  :clean-breadcrumb
  (fn [db _]
    (dissoc db :breadcrumb)))