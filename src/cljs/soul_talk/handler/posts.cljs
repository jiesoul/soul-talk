(ns soul-talk.handler.posts
  (:require [re-frame.core :refer [reg-event-fx reg-event-db subscribe]]
            [ajax.core :refer [POST GET DELETE PUT]]
            [soul-talk.validate :refer [post-errors]]
            [taoensso.timbre :as log]))

(reg-event-db
  :set-posts
  (fn [db [_ {:keys [posts pagination]}]]
    (assoc db :posts posts
              :pagination pagination)))

(reg-event-fx
  :load-posts
  (fn [_ [_ req]]
    (let [{:keys [page pre-page]} req]
      {:http {:method        GET
              :url           (str "/api/posts")
              :ajax-map {:params {:page page
                                  :pre-page pre-page}}
              :success-event [:set-posts]}})))

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

(reg-event-db
  :posts/add-ok
  (fn [db [_ {:keys [post]}]]
    (js/alert "add successful!!")
    (assoc db :admin/posts conj post)))

(reg-event-fx
  :posts/add-error
  (fn [_ [_ {:keys [response]}]]
    {:dispatch [:set-error (:message response)]}))

(reg-event-fx
  :posts/add
  (fn [_ [_ {:keys [category] :as post}]]
    (if-let [error (post-errors post)]
      {:dispatch [:set-error (str (map second error))]}
      {:http {:method        POST
              :url           "/api/admin/posts"
              :ajax-map      {:params (assoc post :category (js/parseInt category))}
              :success-event [:posts/add-ok]
              :error-event [:posts/add-error]}})))

(reg-event-fx
  :posts/edit-ok
  (fn [_ _]
    (js/alert "edit successful!!")
    {:reload-page true}))

(reg-event-fx
  :posts/edit-error
  (fn [_ [_ {:keys [response]}]]
    {:dispatch [:set-error (:message response)]}))

(reg-event-fx
  :posts/edit
  (fn [_ [_ {:keys [id counter] :as post}]]
    (if-let [error (post-errors post)]
      {:dispatch [:set-error (str (map second error))]}
      {:http {:method        PUT
              :url           (str "/api/admin/posts/" id)
              :ajax-map      {:params post}
              :success-event [:posts/edit-ok]
              :error-event   [:posts/edit-error]}})))

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

(reg-event-fx
  :posts/delete-ok
  (fn [_ _]
    (js/alert "delete successful")
    {:reload-page true}))

(reg-event-db
  :posts/delete-error
  (fn [_ _]
    (js/alert "delete fail")))

(reg-event-fx
  :posts/delete
  (fn [_ [_ id]]
    (if (js/confirm "你确定要删除这篇文章吗？")
      {:http {:method        DELETE
              :url           (str "/api/admin/posts/" id)
              :success-event [:posts/delete-ok]
              :error-event   [:posts/delete-error]}})))

(reg-event-fx
  :posts/publish-ok
  (fn [_ _]
    (js/alert "publish successful")
    {:reload-page true}))

(reg-event-fx
  :posts/publish-error
  (fn [_ _]
    (js/alert "publish failed")))

(reg-event-fx
  :posts/publish
  (fn [_ [_ id]]
    {:http {:method PUT
            :url (str "/api/admin/posts/" id "/publish")
            :success-event [:posts/publish-ok]
            :error-event [:posts/publish-error]}}))

(reg-event-db
  :set-posts-archives
  (fn [db [_ {:keys [archives]}]]
    (assoc db :posts-archives archives)))

(reg-event-fx
  :load-posts-archives
  (fn [_ _]
    {:http {:method GET
            :url "/api/posts/archives"
            :success-event [:set-posts-archives]}}))