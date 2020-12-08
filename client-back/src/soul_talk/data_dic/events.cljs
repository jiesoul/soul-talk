(ns soul-talk.data-dic.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
            [ajax.core :refer [GET POST DELETE PUT PATCH]]
            [clojure.string :as str]
            [soul-talk.db :refer [site-uri]]))

(reg-event-db
  :data-dices/set-query-params
  (fn [db [_ key value]]
    (assoc-in db [:data-dices/query-params key] value)))

(reg-event-db
  :data-dices/clean-query-params
  (fn [db _]
    (dissoc db :data-dices/query-params)))

(reg-event-db
  :data-dices/load-page-ok
  (fn [db [_ {:keys [data-dices pagination query-params]}]]
    (assoc db :data-dices data-dices :pagination pagination :data-dices/query-params query-params)))

(reg-event-fx
  :data-dices/load-page
  (fn [_ [_ params]]
    {:http {:method        GET
            :url           (str site-uri "/data-dices")
            :ajax-map      {:params params}
            :success-event [:data-dices/load-page-ok]}}))

(reg-event-db
  :data-dices/load-data-dic-ok
  (fn [db [_ {:keys [data-dic]}]]
    (assoc db :data-dic data-dic)))

(reg-event-fx
  :data-dices/load-data-dic
  (fn [_ [_ id]]
    {:http {:method GET
            :url (str site-uri "/data-dices/" id)
            :success-event [:data-dices/load-data-dic-ok]}}))


(reg-event-db
  :data-dices/set-attr
  (fn [db [_ key value]]
    (assoc-in db [:data-dic key] value)))

(reg-event-db
  :data-dices/add-ok
  (fn [db [_ {:keys [data-dic]}]]
    (let [data-dices (:data-dices db)]
      (assoc db :success "保存成功" :data-dices (conj data-dices data-dic)))))

(reg-event-fx
  :data-dices/add
  (fn [_ [_ data-dic]]
    {:http {:method        POST
            :url           (str site-uri "/data-dices")
            :ajax-map      {:params data-dic}
            :success-event [:data-dices/add-ok]}}))

(reg-event-db
  :data-dices/update-ok
  (fn [db [_ {:keys [data-dic]}]]
    (assoc db :success "保存成功")))

(reg-event-fx
  :data-dices/update
  (fn [_ [_ data-dic]]
    {:http {:method        PATCH
            :url           (str site-uri "/data-dices")
            :ajax-map      {:params data-dic}
            :success-event [:data-dices/update-ok]}}))

(reg-event-db
  :data-dices/delete-ok
  (fn [db [_ id]]
    (let [data-dices (:data-dices db)
          data-dices (remove #(= id (:id %)) data-dices)]
      (assoc db :success "删除成功" :data-dices data-dices))))

(reg-event-fx
  :data-dices/delete
  (fn [_ [_ id]]
    {:http {:method  DELETE
            :url (str site-uri "/data-dices/" id)
            :success-event [:data-dices/delete-ok id]}}))

(reg-event-db
  :data-dices/clean-data-dic
  (fn [db _]
    (dissoc db :data-dic)))

(reg-event-db
  :data-dices/clean
  (fn [db _]
    (dissoc db :data-dices :data-dices/query-params :data-dic)))

