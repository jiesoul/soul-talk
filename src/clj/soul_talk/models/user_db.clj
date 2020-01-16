(ns soul-talk.models.user-db
  (:require [clojure.java.jdbc :as sql]
            [soul-talk.models.db :refer [*db*]]
            [taoensso.timbre :as log]))


(defn save-user! [user]
  (sql/insert! *db* :users user))

(defn find-by-id [id]
  (sql/query *db* ["SELECT * FROM users WHERE id = ? " id]))

(defn select-user [id]
  (log/debug "id: " id)
  (sql/query *db* ["SELECT * FROM users where email = ? " id]
             {:result-set-fn first}))

(defn select-all-users []
  (sql/query *db* ["SELECT * from users"]))

(defn update-login-time [{:keys [email last-time]}]
  (sql/update! *db* :users {:last_login last-time} ["email = ?" email]))


(defn change-pass! [{:keys [email pass-new]}]
  (sql/update! *db* :users {:password pass-new} ["email = ?" email]))

(defn save-user-profile! [{:keys [email name]}]
  (sql/update! *db* :users {:name name} ["email = ?" email]))

(defn count-users []
  (:count
    (first
      (sql/query *db*
        ["SELECT count(email) as count from users"]))))