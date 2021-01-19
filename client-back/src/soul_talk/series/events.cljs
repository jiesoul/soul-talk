(ns soul-talk.series.events
  (:require [re-frame.core :refer [reg-event-db reg-event-fx]]
            [ajax.core :refer [GET POST PATCH PUT DELETE]]
            [soul-talk.db :refer [site-uri]]
            [clojure.string :as str]))

(reg-event-db
  :series/init
  (fn [db _]
    (-> db
      (assoc :series/add-dialog false :series/update-dialog false :series/delete-dialog false)
      (dissoc :series/list :series/pagination :series/query-params))))

(reg-event-db
  :series/set-add-dialog
  (fn [db [_ value]]
    (assoc-in db :series/add-dialog value)))

(reg-event-db
  :series/set-update-dialog
  (fn [db [_ value]]
    (assoc-in db :series/update-dialog value)))

(reg-event-db
  :series/set-delete-dialog
  (fn [db [_ value]]
    (assoc-in db :series/delete-dialog value)))

(reg-event-db
  :series/load-all-ok
  (fn [db [_ {:keys [series pagination]}]]
    (assoc db :series/list series :series/pagination pagination)))

(reg-event-fx
  :series/load-all
  (fn [_ params]
    {:http {:method        GET
            :url           (str site-uri "/series")
            :ajax-map      {:params params}
            :success-event [:series/load-all-ok]}}))

(reg-event-db
  :series/load-page-ok
  (fn [db [_ {:keys [series query-params pagination]}]]
    (assoc db :series/list series
              :series/query-params query-params
              :series/pagination pagination)))

(reg-event-fx
  :series/load-page
  (fn [_ params]
    (println "params: " params)
    {:http {:method GET
            :url (str site-uri "/series")
            :ajax-map {:params params}
            :success-event [:series/load-page-ok]}}))

(reg-event-db
  :series/add-ok
  (fn [db [_ {:keys [series]}]]
    (let [series-list (:series/list db)]
      (assoc db :success "保存成功"
                :series/list (conj series-list series)))))

(reg-event-fx
  :series/add
  (fn [_ [_ {:keys [name] :as series}]]
    (if (str/blank? name)
      {:dispatch [:set-error "名称不能为空"]}
      {:http {:method        POST
              :url           (str site-uri "/series")
              :ajax-map      {:params series}
              :success-event [:series/add-ok]}})))

(reg-event-db
  :series/load-ok
  (fn [db [_ {:keys [series]}]]
    (assoc db :series series)))

(reg-event-fx
  :series/load
  (fn [_ [_ id]]
    {:http {:method GET
            :url (str site-uri "/series/" id)
            :success-event [:series/load-ok]}}))

(reg-event-fx
  :series/update
  (fn [_ [_ {:keys [name] :as series}]]
    (if (str/blank? name)
      {:dispatch [:set-error "名称不能为空"]}
      {:http {:method        PATCH
              :url           (str site-uri "/series")
              :ajax-map      {:params series}
              :success-event [:set-success "保存成功"]}})))

(reg-event-db
  :series/delete-ok
  (fn [db [_ id]]
    (let [series-list (:series/list db)
          series-list(remove #(= id (:id %)) series-list)]
      (assoc db :success "删除成功" :series/list series-list))))


(reg-event-fx
  :series/delete
  (fn [_ [_ id]]
    {:http {:method  DELETE
            :url (str site-uri "/series/" id)
            :success-event [:series/delete-ok id]}}))

(reg-event-db
  :series/clean-series
  (fn [db _]
    (dissoc db :series)))

