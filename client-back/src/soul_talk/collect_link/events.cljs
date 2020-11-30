(ns soul-talk.collect-link.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
            [ajax.core :refer [GET POST DELETE PUT]]
            [clojure.string :as str]
            [soul-talk.db :refer [site-uri]]))

(reg-event-db
  :collect-links/load-all-ok
  (fn [db [_ {:keys [collect-links pagination]}]]
    (assoc db :collect-links collect-links :pagination pagination)))

(reg-event-fx
  :collect-links/load-all
  (fn [_ params]
    (js/console.log "query params: " params)
    {:http {:method        GET
            :url           (str site-uri "/collect-links")
            :ajax-map      {:params params}
            :success-event [:collect-links/load-all-ok]}}))

(reg-event-db
  :collect-links/add-ok
  (fn [db [_ {:keys [collect-link]}]]
    (js/console.log "body: " collect-link)
    (assoc db :success "add a collect-link ok")))

(reg-event-fx
  :collect-links/add
  (fn [_ [_ {:keys [name] :as collect-link}]]
    (if (str/blank? name)
      {:dispatch [:set-error "名称不能为空"]}
      {:http {:method        POST
              :url           (str site-uri "/collect-links")
              :ajax-map      {:params collect-link}
              :success-event [:collect-links/add-ok]}})))

(reg-event-db
  :collect-links/delete-ok
  (fn [db [_ id]]
    (let [collect-links (:collect-links db)
          collect-links (remove #(= id (:id %)) collect-links)]
      (assoc db :success "删除成功" :collect-links collect-links))))

(reg-event-fx
  :collect-links/delete
  (fn [_ [_ id]]
    {:http {:method  DELETE
            :url (str site-uri "/collect-links/" id)
            :success-event [:collect-links/delete-ok id]}}))

(reg-event-db
  :collect-links/clean-collect-link
  (fn [db _]
    (dissoc db :collect-link)))
 
