(ns soul-talk.role.events
  (:require [re-frame.core :as rf]
            [soul-talk.db :refer [site-uri]]
            [soul-talk.utils :as utils]))

(rf/reg-event-fx
  :roles/load-menus-ok
  (fn [{:keys [db]} [_ {:keys [role-menus]}]]
    (js/console.log "=========" role-menus)
    (let [menu-ids (map :menu_id role-menus)]
      {:db         (assoc-in db [:user :role-menus] role-menus)
       :dispatch-n (list [:menus/load-menus menu-ids])})))

(rf/reg-event-fx
  :roles/load-menus
  (fn [_ [_ ids]]
    {:http {:method        get
            :url           (str site-uri "/roles/menus")
            :ajax-map      {:params {:ids ids}}
            :success-event [:roles/load-menus-ok]}}))