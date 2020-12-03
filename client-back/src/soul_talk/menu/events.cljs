(ns soul-talk.menu.events
  (:require [re-frame.core :as rf]
            [soul-talk.db :refer [site-uri]]))

(rf/reg-event-fx
  :menus/load-menus-ok
  (fn [{:keys [db]} [_ {:keys [menus]}]]
    {:db (assoc db :menus menus)}))

(rf/reg-event-fx
  :menus/load-menus
  (fn [_ [_ ids]]
    {:http {:method get
            :url (str site-uri "/menus?ids=" ids)
            :success-event [:menus/load-menus-ok]}}))
