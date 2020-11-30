(ns soul-talk.role.db
  (:require [soul-talk.database.db :refer [*db*]]
            [next.jdbc.result-set :as rs-set]
            [next.jdbc.sql :as sql]))

(defn get-role [id]
  (sql/get-by-id *db* :role id))

(defn get-roles-by-user-id [user-id]
  (sql/query *db*
    ["select * from role where id in (select role_id from user_role where user_id = ?)" user-id]
    {:builder-fn rs-set/as-unqualified-maps}))