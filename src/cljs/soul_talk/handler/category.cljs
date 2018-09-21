(ns soul-talk.handler.category
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
            [ajax.core :refer [POST GET]]
            [clojure.string :as str]))


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

(reg-event-db
  :categories-add-ok
  (fn [db [_ {:keys [category]}]]
    (js/alert "Add successful1")
    (update db :categories conj category)))

(reg-event-fx
  :categories/add
  (fn [_ [_ {:keys [name] :as category}]]
    (if (str/blank? name)
      {:dispatch [:set-error "名称不能为空"]}
      {:http {:method        POST
              :url           "/api/admin/categories/add"
              :ajax-map      {:params category}
              :success-event [:categories-add-ok]}})))

