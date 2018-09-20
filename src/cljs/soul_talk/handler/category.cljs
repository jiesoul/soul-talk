(ns soul-talk.handler.category
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
            [ajax.core :refer [POST GET]]))


(reg-event-db
  :set-categories
  (fn [db [_ {:keys [categories]}]]
    (assoc db :categories categories)))


(reg-event-fx
  :load-categories
  (fn [_ _]
    {:http {:method GET
            :url "/api/categories"
            :success-event [:set-categories]}}))
