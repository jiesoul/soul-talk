(ns soul-talk.data-dic.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
            [ajax.core :refer [GET POST DELETE PUT PATCH]]
            [clojure.string :as str]
            [soul-talk.db :refer [site-uri]]))

(reg-event-db
  :data-dic/init
  (fn [db _]
    (-> db
      (dissoc :data-dic/list :data-dic/query-params :data-dic/pagination :data-dic/edit)
      (assoc :data-dic/delete-dialog false))))

(reg-event-db
  :data-dic/set-query-params
  (fn [db [_ key value]]
    (assoc-in db [:data-dic/query-params key] value)))

(reg-event-db
  :data-dic/clean-query-params
  (fn [db _]
    (dissoc db :data-dic/query-params)))

(reg-event-db
  :data-dic/set-delete-dialog
  (fn [db [_ value]]
    (assoc db :data-dic/delete-dialog value)))

(reg-event-db
  :data-dic/load-page-ok
  (fn [db [_ {:keys [data-dices pagination query-params]}]]
    (assoc db :data-dic/list data-dices :pagination pagination :data-dic/query-params query-params)))

(reg-event-fx
  :data-dic/load-page
  (fn [_ [_ params]]
    {:http {:method        GET
            :url           (str site-uri "/data-dices")
            :ajax-map      {:params params}
            :success-event [:data-dic/load-page-ok]}}))

(reg-event-db
  :data-dic/load-data-dic-ok
  (fn [db [_ {:keys [data-dic]}]]
    (assoc db :data-dic/edit data-dic)))

(reg-event-fx
  :data-dic/load-data-dic
  (fn [_ [_ id]]
    {:http {:method GET
            :url (str site-uri "/data-dices/" id)
            :success-event [:data-dic/load-data-dic-ok]}}))

(reg-event-db
  :data-dic/clean-edit
  (fn [db _]
    (dissoc db :data-dic/edit)))

(reg-event-db
  :data-dic/set-attr
  (fn [db [_ attr]]
    (update-in db [:data-dic/edit] merge attr)))

(reg-event-fx
  :data-dic/save
  (fn [_ [_ data-dic]]
    {:http {:method        POST
            :url           (str site-uri "/data-dices")
            :ajax-map      {:params data-dic}
            :success-event [:set-success "保存成功"]}}))

(reg-event-fx
  :data-dic/update
  (fn [_ [_ data-dic]]
    {:http {:method        PATCH
            :url           (str site-uri "/data-dices")
            :ajax-map      {:params data-dic}
            :success-event [:set-success "保存成功"]}}))

(reg-event-fx
  :data-dic/delete-ok
  (fn [{:keys [db]} [_ id]]
    (let [data-dices (:data-dic/list db)
          data-dices (remove #(= id (:id %)) data-dices)]
      {:db       (assoc db :data-dic/list data-dices)
       :dispatch [:set-success "删除成功"]})))

(reg-event-fx
  :data-dic/delete
  (fn [_ [_ id]]
    {:http {:method  DELETE
            :url (str site-uri "/data-dices/" id)
            :success-event [:data-dic/delete-ok id]}}))

