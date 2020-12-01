(ns soul-talk.site-info.events
  (:require [re-frame.core :as rf]
            [ajax.core :refer [GET POST PATCH PUT DELETE]]
            [soul-talk.db :refer [site-uri]]
            [soul-talk.common.local-storage :as storage]))

(rf/reg-fx
  :set-site-info!
  (fn [site-info]
    (storage/set-item! :site-info site-info)))

(rf/reg-fx
  :clean-site-info!
  (fn []
    (storage/remove-item! :site-info)))

(rf/reg-event-db
  :site-info/load-ok
  (fn [db [_ {:keys [site-info]}]]
    (assoc db :site-info site-info)))

(rf/reg-event-fx
  :site-info/load
  (fn [_ [_ id]]
    {:http {:method GET
            :url (str site-uri "/site-info/" id)
            :success-event [:site-info/load-ok]}}))

(rf/reg-event-db
  :site-info/update-ok
  (fn [db [_ {:keys [site-info]}]]
    (assoc db :site-info site-info)))

(rf/reg-event-fx
  :site-info/update
  (fn [_ [_ site-info]]
    {:http {:method        PATCH
            :url           (str site-uri "/site-info")
            :ajax-map      {:params site-info}
            :success-event [:series/update-ok]}}))