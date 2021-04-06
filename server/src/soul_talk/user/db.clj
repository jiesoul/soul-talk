(ns soul-talk.user.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as rs-set]
            [soul-talk.database.db :refer [*db*]]
            [crypto.random :refer [base64]]
            [taoensso.timbre :as log]))

(defn find-by [query-map]
  (let [users (sql/find-by-keys *db* :users query-map)]
    (some-> users
      first)))

(defn find-by-id [id]
  (sql/get-by-id *db* :users id))

(defn find-by-email [email]
  (find-by {:email email}))

(defn insert-user! [user]
  (sql/insert! *db* :users user))

(defn select-all-users []
  (sql/query *db* ["SELECT * from users"]))

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

(defn gen-where [{:keys [name]}]
  (let [[where coll] [(str " where name like ?")
                      [(str "%" name "%")]]]
    [where coll]))

(defn load-users-page [{:keys [offset per_page]} params]
  (let [[where coll] (gen-where params)
        query-sql (str "select * from users " where " offset ? limit ?")
        users (sql/query *db*
                (into [query-sql] (conj coll offset per_page))
                {:builder-fn rs-set/as-unqualified-maps})
        count-sql (str "select count(0) as c from users " where)
        total (:c
                (first
                  (sql/query *db*
                    (into [count-sql] coll))))]
    [users total]))

(defn key-gen-where [{:keys [name]}]
  (let [[where coll] [(str " where u.name like ? ") [(str "%" name "%")]]]
    [where coll]))

(defn load-users-auth-keys-page [{:keys [offset per_page]} params]
  (let [[where coll] (key-gen-where params)
        query-sql (str "select a.*, u.name from auth_token a left join users u on a.user_id = u.id " where " offset ? limit ?")
        auth-keys (sql/query *db* (into [query-sql] (conj coll offset per_page)))
        count-sql (str "select count(0) as c from auth_token a left join users u on a.user_id = u.id " where)
        total (:c (first (sql/query *db* (into [count-sql] coll))))]
    (log/info "total: " total)
    [auth-keys total]))

(defn delete-user! [id]
  (sql/delete! *db* :users ["id = ?" id]))

(defn get-user-roles [id]
  (sql/query *db*
    ["select * from user_role where user_id = ? " id]
    {:builder-fn rs-set/as-unqualified-maps}))

