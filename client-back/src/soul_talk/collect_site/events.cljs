(ns soul-talk.collect-site.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
            [ajax.core :refer [GET POST DELETE PUT]]
            [clojure.string :as str]
            [soul-talk.db :refer [site-uri]]))

(reg-event-db
  :collect-sites/load-all-ok
  (fn [db [_ {:keys [collect-sites pagination]}]]
    (assoc db :collect-sites collect-sites :pagination pagination)))

(reg-event-fx
  :collect-sites/load-all
  (fn [_ params]
    {:http {:method        GET
            :url           (str site-uri "/collect-sites")
            :ajax-map      {:params params}
            :success-event [:collect-sites/load-all-ok]}}))

(reg-event-db
  :collect-sites/add-ok
  (fn [db [_ {:keys [collect-site]}]]
    (assoc db :success "add a collect-site ok")))

(reg-event-fx
  :collect-sites/add
  (fn [_ [_ {:keys [name] :as collect-site}]]
    (if (str/blank? name)
      {:dispatch [:set-error "名称不能为空"]}
      {:http {:method        POST
              :url           (str site-uri "/collect-sites")
              :ajax-map      {:params collect-site}
              :success-event [:collect-sites/add-ok]}})))

(reg-event-db
  :collect-sites/delete-ok
  (fn [db [_ id]]
    (let [collect-sites (:collect-sites db)
          collect-sites (remove #(= id (:id %)) collect-sites)]
      (assoc db :success "删除成功" :collect-sites collect-sites))))

(reg-event-fx
  :collect-sites/delete
  (fn [_ [_ id]]
    {:http {:method  DELETE
            :url (str site-uri "/collect-sites/" id)
            :success-event [:collect-sites/delete-ok id]}}))

(reg-event-db
  :collect-sites/clean-collect-site
  (fn [db _]
    (dissoc db :collect-site)))
 
