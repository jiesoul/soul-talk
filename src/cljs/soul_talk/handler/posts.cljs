(ns soul-talk.handler.posts
  (:require [re-frame.core :refer [reg-event-fx reg-event-db subscribe]]
            [ajax.core :refer [POST GET]]
            [soul-talk.validate :refer [post-errors]]
            [taoensso.timbre :as log]))

(reg-event-db
  :admin/set-posts
  (fn [db [_ {:keys [posts]}]]
    (assoc db :admin/posts posts)))


(reg-event-fx
  :admin/load-posts
  (fn [_ _]
    {:http {:method GET
            :url "/api/admin/posts"
            :success-event [:admin/set-posts]}}))

(reg-event-fx
  :posts-add-ok
  (fn [_ _]
    (js/alert "add successful!!")
    {:reload-page true}))


(reg-event-fx
  :posts-add-error
  (fn [_ [_ {:keys [response]}]]
    {:dispatch [:set-error (:message response)]}))

(reg-event-fx
  :posts/add
  (fn [_ [_ post]]
    (if-let [error (post-errors post)]
      {:dispatch [:set-error (str (map second error))]}
      {:http {:method        POST
              :url           "/api/admin/posts/add"
              :ajax-map      {:params post}
              :success-event [:posts-add-ok]
              :error-event [:posts-add-error]}})))

(reg-event-db
  :set-post
  (fn [db [_ {post :post}]]
    (assoc db :post post)))

(reg-event-fx
  :load-post
  (fn [_ [_ id]]
    {:http {:method GET
            :url    (str "/api/posts/" id)
            :success-event [:set-post]}}))