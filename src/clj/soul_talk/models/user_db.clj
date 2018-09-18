(ns soul-talk.models.user-db
  (:require [clojure.java.jdbc :as sql]
            [soul-talk.models.db :refer [db-spec]]
            [taoensso.timbre :as log]))


(defn save-user! [user]
  (sql/insert! db-spec :users user))

(defn select-user [id]
  (sql/query db-spec ["SELECT * FROM users where email = ? " id]
             {:result-set-fn first}))

(defn select-all-users []
  (sql/query db-spec ["SELECT * from users"]))

(defn update-login-time [{:keys [email last-time]}]
  (sql/update! db-spec :users {:last_login last-time} ["email = ?" email]))


(defn change-pass! [{:keys [email pass-new]}]
  (sql/update! db-spec :users {:password pass-new} ["email = ?" email]))

(defn save-user-profile! [{:keys [email name]}]
  (sql/update! db-spec :users {:name name} ["email = ?" email]))