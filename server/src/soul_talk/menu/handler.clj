(ns soul-talk.menu.handler
  (:require [soul-talk.menu.db :as menu-db]
            [soul-talk.pagination :as p]
            [soul-talk.menu.spec :as spec]
            [soul-talk.utils :as utils]
            [java-time.local :as l]))

(def create-menu spec/create-menu)
(def update-menu spec/update-menu)

(defn save-menu! [menu]
  (let [now (l/local-date-time)
        menu (menu-db/save-menu! (assoc menu :create_at now :update_at now))]
    (utils/ok {:menu menu})))

(defn update-menu! [menu]
  (let [now (l/local-date-time)
        menu (menu-db/update-menu! (assoc menu :update_at now))]
    (utils/ok {:menu menu})))

(defn delete-menu! [id]
  (let [rs (menu-db/delete-menu! id)]
    (utils/ok "删除成功")))

(defn load-menus-page [req]
  (let [pagination (p/create req)
        params (:params req)
        [menus total] (menu-db/load-menus-page pagination params)
        pagination (p/create-total pagination total)]
    (utils/ok {:menus menus
               :pagination pagination
               :query-params params})))

(defn get-menu-by-id [id]
  (let [menu (menu-db/get-menu id)]
    (utils/ok {:menu menu})))

(defn get-menus-by-ids [ids]
  (let [menus (menu-db/get-menus-by-ids ids)]
    (utils/ok {:menus menus})))
