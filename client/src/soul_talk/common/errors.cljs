(ns soul-talk.common.errors
  (:require [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx]]
            [taoensso.timbre :as log]))

(reg-event-fx
  :ajax-error
  (fn [db [_ {:keys [response status status-text] :as resp}]]
    (js/console.log "ajax-error: " resp)
    {:dispatch-n (condp = status
                   0 (list [:set-error status-text])
                   401 (list [:set-error (:message response)] [:logout])
                   404 (list [:set-error "资源未找到"])
                   (list [:set-error (:message response)]))}))


(reg-event-db
  :set-error
  (fn [db [_ error]]
    (assoc db :error error)))

(reg-event-db
  :clean-error
  (fn [db _]
    (dissoc db :error)))