(ns soul-talk.menu.db
  (:require [soul-talk.database.db :refer [*db*]]
            [next.jdbc.result-set :as rs-set]
            [next.jdbc.sql :as sql]))

(defn get-menu-by-role-id [role-ids]
  (sql/query *db*
    ["select * from menu where id in (select menu_id from role_menu where role_id = ANY(?))"
     (int-array role-ids)]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn save-menu! [menu]
  (sql/insert! *db* :menu menu {:builder-fn rs-set/as-unqualified-maps}))

(defn update-menu! [menu]
  (sql/update! *db*
    :menu
    (select-keys menu [:name :pid :url :note])
    [" id = ? " (:id menu)]))

(defn delete-menu! [id]
  (sql/delete! *db* :menu ["id = ?" id]))

(defn get-menu [id]
  (sql/get-by-id *db* :menu id))

(defn get-menus-by-ids [ids]
  (sql/query *db*
    ["select * from menu where id = any(?)" (int-array ids)]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn gen-where [{:keys [name]}]
  (let [[where-str coll] [(str " where 1=1 ") []]
        [where-str coll] (if name
                           [where-str coll]
                           [(str where-str " and name like ?") (conj coll (str "%" name "%"))])]
    [where-str coll]))

(defn load-menus-page [{:keys [offset per_page]} params]
  (let [[where coll] (gen-where params)
        query-sql (str "select * from menu " where " offset ? limit ?")
        menus (sql/query *db*
                (into query-sql (conj coll offset per_page))
                {:builder-fn rs-set/as-unqualified-maps})
        count-sql (str "select count(1) as c from menu " where)
        total     (:c (first (sql/query *db*
                               (into count-sql coll))))]
    [menus total]))

