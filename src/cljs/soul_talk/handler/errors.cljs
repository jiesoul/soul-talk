(ns soul-talk.handler.errors
  (:require [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx]]))

(reg-event-fx
  :ajax-error
  (fn [_ [_ {:keys [response status] :as resp}]]
    (js/console.log resp)
    {:dispatch [:set-error (:message response)]}))


(reg-event-db
  :set-error
  (fn [db [_ error]]
    (assoc db :error error)))

(reg-event-db
  :clean-error
  (fn [db _]
    (dissoc db :error)))