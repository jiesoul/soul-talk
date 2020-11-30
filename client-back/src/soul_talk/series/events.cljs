(ns soul-talk.series.events
  (:require [re-frame.core :refer [reg-event-db reg-event-fx]]
            [ajax.core :refer [GET POST PATCH PUT DELETE]]
            [soul-talk.db :refer [site-uri]]
            [clojure.string :as str]))

(reg-event-db
  :series/load-all-ok
  (fn [db [_ {:keys [series pagination]}]]
    (assoc db :series-list series :pagination pagination)))

(reg-event-fx
  :series/load-all
  (fn [_ params]
    (js/console.log "===== query params: " params)
    {:http {:method        GET
            :url           (str site-uri "/series")
            :ajax-map      {:params params}
            :success-event [:series/load-all-ok]}}))

(reg-event-db
  :series/add-ok
  (fn [{:keys [series-list] :as db} [_ {:keys [series]}]]
    (assoc db :success "add a series ok"
              :series-list (conj series-list series))))

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


(reg-event-db
  :series/add-ok
  (fn [db [_ {:keys [series]}]]
    (js/console.log "body: " series)
    (assoc db :success "add a series ok")))

(reg-event-fx
  :series/update
  (fn [_ [_ {:keys [name] :as series}]]
    (if (str/blank? name)
      {:dispatch [:set-error "名称不能为空"]}
      {:http {:method        PATCH
              :url           (str site-uri "/series")
              :ajax-map      {:params series}
              :success-event [:series/update-ok]}})))

(reg-event-db
  :series/delete-ok
  (fn [db [_ id]]
    (let [series-list (:series-list db)
          series-list(remove #(= id (:id %)) series-list)]
      (assoc db :success "删除成功" :series-list series-list))))


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

