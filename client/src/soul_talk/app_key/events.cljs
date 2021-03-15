(ns soul-talk.app-key.events
  (:require [re-frame.core :refer [reg-event-db reg-event-fx subscribe]]
            [ajax.core :refer [GET POST PATCH DELETE PUT]]
            [soul-talk.db :refer [site-uri]]
            [clojure.string :as str]))

(reg-event-db
  :app-keys/load-all-ok
  (fn [db [_ {:keys [app-keys pagination]}]]
    (assoc db :app-keys app-keys :pagination pagination)))

(reg-event-fx
  :app-keys/load-all
  (fn [_ params]
    {:http {:method        GET
            :url           (str site-uri "/app-keys")
            :ajax-map      {:params params}
            :success-event [:app-keys/load-all-ok]}}))

(reg-event-db
  :app-keys/new-ok
  (fn [db [_ {:keys [message body]}]]
    (assoc db :success "add a tag ok")))

(reg-event-fx
  :app-keys/new
  (fn [_ [_ {:keys [name] :as tag}]]
    (if (str/blank? name)
      {:dispatch [:set-error "名称不能为空"]}
      {:http {:method        POST
              :url           (str site-uri "/app-keys")
              :ajax-map      {:params tag}
              :success-event [:app-keys/new-ok]}})))

(reg-event-db
  :app-keys/delete-ok
  (fn [db [_ id]]
    (let [app-keys (:app-keys db)
          app-keys (remove #(= id (:id %)) app-keys)]
      (assoc db :success "删除成功" :app-keys app-keys))))

(reg-event-fx
  :app-keys/delete
  (fn [_ [_ id]]
    {:http {:method  DELETE
            :url (str site-uri "/app-keys/" id)
            :success-event [:app-keys/delete-ok id]}}))

(reg-event-db
  :app-keys/clean-tag
  (fn [db _]
    (dissoc db :tag)))