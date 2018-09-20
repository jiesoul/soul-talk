(ns soul-talk.models.post-db
  (:require [soul-talk.models.db :refer [db-spec]]
            [clojure.java.jdbc :as sql]))

(defn save-post! [post]
  (sql/insert! db-spec :posts post))

(defn update-post! [{:keys [id] :as post}]
  (sql/update! db-spec :posts post ["id = ?" id]))


(defn get-posts []
  (sql/query db-spec :posts ["select * from posts"]))

(defn get-post [id]
  (sql/query db-spec ["SELECT * FROM posts where id = ? " id]
    {:result-set-fn first}))


(defn delete-post! [id]
  (sql/delete! db-spec :posts ["id = ?" id]))