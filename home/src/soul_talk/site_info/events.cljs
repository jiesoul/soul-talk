(ns soul-talk.site-info.events
  (:require [re-frame.core :as rf]
            [ajax.core :refer [GET POST PATCH PUT DELETE]]
            [soul-talk.db :refer [api-url]]))

(rf/reg-event-db
  :site-info/load-ok
  (fn [db [_ {:keys [site-info]}]]
    (assoc db :site-info site-info)))

(rf/reg-event-fx
  :site-info/load
  (fn [_ [_ id]]
    (js/console.log "init site info")
    {:http {:method GET
            :url (str api-url "/site-info/" id)
            :success-event [:site-info/load-ok]}}))

(rf/reg-event-db
  :site-info/set-attr
  (fn [db [_ key value]]
    (assoc-in db [:site-info key] value)))

(rf/reg-event-db
  :site-info/update-ok
  (fn [db [_ {:keys [site-info]}]]
    (assoc db :success "保存成功" :site-info site-info)))

(rf/reg-event-fx
  :site-info/update
  (fn [_ [_ site-info]]
    {:http {:method        PATCH
            :url           (str api-url "/site-info")
            :ajax-map      {:params site-info}
            :success-event [:site-info/update-ok]}}))