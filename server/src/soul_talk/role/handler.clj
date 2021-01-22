(ns soul-talk.role.handler
  (:require [soul-talk.role.db :as role-db]
            [soul-talk.pagination :as p]
            [soul-talk.role.spec :as spec]
            [soul-talk.utils :as utils]
            [clojure.tools.logging :as log]))

(def create-role spec/create-role)
(def update-role spec/update-role)

(defn save-role! [role]
  (let [role (role-db/save-role! role)]
    (utils/ok {:role role})))

(defn update-role! [role]
  (let [role (role-db/update-role! role)]
    (utils/ok {:role role})))

(defn delete-role! [id]
  (let [rs (role-db/delete-role! id)]
    (utils/ok "删除成功")))

(defn load-roles-page [req]
  (let [pagination (p/create req)
        params (:params req)
        [roles total] (role-db/load-roles-page pagination params)
        pagination (p/create-total pagination total)]
    (utils/ok {:roles roles
               :pagination pagination
               :params params})))

(defn get-role [id]
  (let [role (role-db/get-role-by-id id)
        role-menus (role-db/get-role-menus-by-role-id id)]
    (utils/ok {:role (assoc role :menus-ids (map :menu_id role-menus))})))

(defn get-role-menus [id]
  (let [role-menus (role-db/get-role-menus-by-role-id id)]
    (utils/ok {:role-menus role-menus})))

(defn get-role-menus-by-ids [ids]
  (let [role-menus (role-db/get-role-menus-by-ids ids)]
    (log/debug "===== ids:" ids)
    (utils/ok {:role-menus role-menus})))