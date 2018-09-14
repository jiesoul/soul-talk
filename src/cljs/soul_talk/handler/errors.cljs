(ns soul-talk.handler.errors
  (:require [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx]]))

(reg-event-fx
  :ajax-error
  (fn [_ [_ {status :status
             {error :message} :response}]]
    {:dispatch [:set-error error]}))


(reg-event-db
  :set-error
  (fn [db [_ error]]
    (assoc db :error error)))

(reg-event-db
  :clean-error
  (fn [db _]
    (dissoc db :error)))