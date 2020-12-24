(ns soul-talk.role.db
  (:require [soul-talk.database.db :refer [*db*]]
            [next.jdbc.result-set :as rs-set]
            [next.jdbc.sql :as sql]
            [clojure.string :as str]))

(defn save-role! [role]
  (sql/insert! *db* :role role {:builder-fn rs-set/as-unqualified-maps}))

(defn update-role! [role]
  (sql/update! *db* :role (select-keys role [:name :note]) [" id = ? " (:id role)]))

(defn delete-role! [id]
  (sql/delete! *db* :role ["id = ?" id]))

(defn get-role-by-id [id]
  (sql/get-by-id *db* :role id {:builder-fn rs-set/as-unqualified-maps}))

(defn get-role-menus-by-role-id [id]
  (sql/query *db*
    ["select * from menu where id in (select menus_id from role_menu where role_id = ? )" id]))

(defn get-roles-by-ids [ids]
  (sql/query *db*
    ["select * from role where id = any(?)" ids]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn get-roles-by-user-id [user-id]
  (sql/query *db*
    ["select * from role where id in (select role_id from user_role where user_id = ?)" user-id]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn gen-where [{:keys [name]}]
  (let [[where-str coll] [(str " where 1=1 ") []]
        [where-str coll] (if (str/blank? name)
                           [where-str coll]
                           [(str where-str " and name like ?") (conj coll (str "%" name "%"))])]
    [where-str coll]))

(defn load-roles-page [{:keys [offset per_page]} params]
  (let [[where coll] (gen-where params)
        query-sql (str "select * from role " where " offset ? limit ?")
        roles (sql/query *db*
                (into [query-sql] (conj coll offset per_page)))
        count-sql (str "select count(1) as c from role " where)
        total     (:c (first (sql/query *db*
                               (into [count-sql] coll))))]
    [roles total]))

(defn get-role-menus-by-ids [ids]
  (sql/query *db*
    ["select * from role_menu where role_id = any(?)" (int-array ids)]))

