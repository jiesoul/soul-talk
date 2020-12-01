(ns soul-talk.menu.handler
  (:require [soul-talk.menu.db :as menu-db]
            [soul-talk.pagination :as p]
            [soul-talk.menu.spec :as spec]
            [soul-talk.utils :as utils]))

(def create-menu spec/create-menu)
(def update-menu spec/update-menu)

(defn save-menu! [menu]
  (let [menu (menu-db/save-menu! menu)]
    (utils/ok {:menu menu})))

(defn update-menu! [menu]
  (let [menu (menu-db/update-menu! menu)]
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
               :params params})))
