(ns soul-talk.handler.tag
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
            [ajax.core :refer [GET POST]]
            [clojure.string :as str]))


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

(reg-event-db
  :add-tag-ok
  (fn [db [_ {:keys [tag]}]]
    (js/alert "Add successful1")
    (update db :tags conj tag)))

(reg-event-fx
  :tags/add-tag
  (fn [_ [_ {:keys [name] :as tag}]]
    (if (str/blank? name)
      {:dispatch [:set-error "名称不能为空"]}
      {:http {:method        POST
              :url           "/api/admin/tags/add"
              :ajax-map      {:params tag}
              :success-event [:add-tag-ok]}})))
