(ns soul-talk.handler.posts
  (:require [re-frame.core :refer [reg-event-fx reg-event-db subscribe]]
            [ajax.core :refer [POST GET]]))

(reg-event-db
  :admin/set-posts
  (fn [db [_ {:keys [posts]}]]
    (assoc db :admin/posts posts)))


(reg-event-fx
  :admin/load-posts
  (fn [_ _]
    {:http {:method POST
            :url "/api/admin/posts"
            :success-event [:admin/set-posts]}}))