(ns soul-talk.menu.handler
  (:require [soul-talk.menu.db :as menu-db]
            [soul-talk.pagination :as p]
            [soul-talk.menu.spec :as spec]
            [soul-talk.utils :as utils]
            [java-time.local :as l]
            [cambium.core :as log]
            [clojure.string :as str]))

(def create-menu spec/create-menu)
(def update-menu spec/update-menu)

(defn save-menu! [menu]
  (let [now (utils/now)
        create-by (:create_by menu)
        menu (menu-db/save-menu! (assoc menu :create_at now :update_at now :update_by create-by))]
    (utils/ok {:menu menu})))

(defn update-menu! [menu]
  (let [now (utils/now)
        menu (menu-db/update-menu! (assoc menu :update_at now))]
    (utils/ok {:menu menu})))

(defn delete-menu! [id]
  (let [menus (menu-db/get-menu-by-pid id)]
    (if (seq? menus)
      (utils/bad-request "删除失败， 菜单有子菜单")
      (do (menu-db/delete-menu! id)
          (utils/ok "删除成功")))))

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
  (let [ids (str/split ids #",")
        ids (map #(utils/parse-int %) ids)
        menus (menu-db/get-menus-by-ids ids)]
    (utils/ok {:menus menus})))

(defn load-menus-all []
  (let [menus (menu-db/get-all)]
    (utils/ok {:menus menus})))

(defn load-menus-by-pid [pid]
  (let [menus (menu-db/get-menu-by-pid pid)]
    (utils/ok {:menus menus})))
