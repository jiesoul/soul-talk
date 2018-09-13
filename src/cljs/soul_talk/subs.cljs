(ns soul-talk.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
  :db-state
  (fn [db _]
    db))

(defn query [db [event-id]]
  (event-id db))

(rf/reg-sub :home query)


(rf/reg-sub
  :time
  (fn [db _]     ;; db is current app state. 2nd unused param is query vector
    (:time db))) ;; return a query computation over the application state

(rf/reg-sub
  :time-color
  (fn [db _]
    (:time-color db)))
