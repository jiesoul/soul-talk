(ns soul-talk.user.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as rs-set]
            [soul-talk.database.db :refer [*db*]]
            [crypto.random :refer [base64]]
            [taoensso.timbre :as log]))

(defn find-by [query-map]
  (let [users (sql/find-by-keys *db* :users query-map {:builder-fn rs-set/as-unqualified-maps})]
    (some-> users
      first)))

(defn find-by-id [id]
  (sql/get-by-id *db* :users id {:builder-fn rs-set/as-unqualified-maps}))

(defn find-by-email [email]
  (find-by {:email email}))

(defn insert-user! [user]
  (sql/insert! *db* :users user))

(defn select-all-users []
  (sql/query *db* ["SELECT * from users"] {:builder-fn rs-set/as-unqualified-maps}))

(defn update-login-time! [{:keys [id last_login_at]}]
  (sql/update! *db* :users {:last_login_at last_login_at} ["id = ?" id]))


(defn update-pass! [{:keys [id password]}]
  (sql/update! *db* :users {:password password} ["id = ?" id]))

(defn save-user-profile! [{:keys [id name] :as user}]
  (sql/update! *db* :users {:name name} ["id = ?" id]))

(defn count-users []
  (:count
    (first
      (sql/query *db*
        ["SELECT count(email) as count from users"]))))

