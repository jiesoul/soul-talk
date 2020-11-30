(ns soul-talk.menu.db
  (:require [soul-talk.database.db :refer [*db*]]
            [next.jdbc.result-set :as rs-set]
            [next.jdbc.sql :as sql]))

(defn get-menu-by-role-id [role-ids]
  (sql/query *db*
    ["select * from menu where id in (select menu_id from role_menu where role_id = ANY(?))"
     (int-array role-ids)]
    {:builder-fn rs-set/as-unqualified-maps}))