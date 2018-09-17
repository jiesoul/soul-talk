(ns soul-talk.handler.users
  (:require [re-frame.core :refer [reg-event-fx reg-event-db subscribe]]
            [ajax.core :refer [GET]]
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

