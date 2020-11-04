(ns soul-talk.tag.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
            [ajax.core :refer [GET POST DELETE PUT]]
            [clojure.string :as str]
            [soul-talk.db :refer [api-uri]]))

(reg-event-db
  :tags/load-ok
  (fn [db [_ {:keys [data]}]]
    (let [tags (:tags data)]
      (assoc db :tags tags))))

(reg-event-fx
  :tags/load
  (fn [_ _]
    {:http {:method        GET
            :url           (str api-uri "/tags")
            :success-event [:tags/load-ok]}}))

(reg-event-db
  :tags/add-error
  (fn [db [_ {:keys [message]}]]
    (assoc db :error message)))

(reg-event-db
  :tags/add-ok
  (fn [db [_ {:keys [message]}]]
    (assoc db :success message)))

(reg-event-fx
  :add-tags
  (fn [_ [_ {:keys [name] :as tag}]]
    (if (str/blank? name)
      {:dispatch [:set-error "名称不能为空"]}
      {:http {:method        POST
              :url           (str api-uri "/tags")
              :ajax-map      {:params tag}
              :success-event [:tags/add-ok]
              :error-event [:tags/add-error]}})))

(reg-event-db
  :tags/delete-ok
  (fn [db [_ {:keys [message]}]]
    (assoc db :success message)))

(reg-event-fx
  :tags/delete
  (fn [_ [_ id]]
    {:http {:method  DELETE
            :url (str api-uri "/tags/" id)
            :success-event [:tags/delete-ok]
            :error-event [:tags/delete-error]}}))
