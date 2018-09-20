(ns soul-talk.handler.tag
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
            [ajax.core :refer [GET POST]]))


(reg-event-db
  :set-tags
  (fn [db [_ {:keys [tags]}]]
    (assoc db :tags tags)))


(reg-event-fx
  :load-tags
  (fn [_ _]
    {:http {:method GET
            :url "/api/tags"
            :success-event [:set-tags]}}))
