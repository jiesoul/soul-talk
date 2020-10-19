(ns soul-talk.dash.handler
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
            [ajax.core :refer [POST GET]]
            [soul-talk.db :refer [api-uri]]))

(reg-event-db
  :set-dashboard
  (fn [db {:keys [message]}]
    (assoc db :dashboard message)))

(reg-event-fx
  :load-dashboard
  (fn [_ _]
    {:http {:method        GET
            :url           (str api-uri "/dashboard")
            :success-event [:set-dashboard]}}))
