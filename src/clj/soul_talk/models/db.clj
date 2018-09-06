(ns soul-talk.models.db
  (:require [clojure.java.jdbc :as sql]
            [taoensso.timbre :as log]))

(def db-spec {:subprotocol "postgresql"
              :subname "//localhost:5432/soul_talk"
              :user "jiesoul"
              :password "12345678"})

(defn test-db []
  (sql/query db-spec "select 3*5 as result"))

(defn save-user! [user]
  (sql/insert! db-spec :users user))

(defn select-user [id]
  (sql/query db-spec ["SELECT * FROM users where email = ? " id]
             {:result-set-fn first}))

(defn select-all-users []
  (sql/query db-spec ["SELECT * from users"]))

(defn update-login-time [{:keys [email last-time]}]
  (sql/update! db-spec :users {:last_login last-time} ["email = ?" email]))