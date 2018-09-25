(ns soul-talk.models.post-db
  (:require [soul-talk.models.db :refer [*db*]]
            [clojure.java.jdbc :as sql]
            [taoensso.timbre :as log]))

(defn save-post! [post]
  (sql/insert! *db* :posts post))

(defn update-post! [{:keys [id] :as post}]
  (sql/update! *db* :posts post ["id = ?" id]))


(defn get-posts-all []
  (sql/query *db* ["select * from posts order by create_time desc"]))

(defn get-post-by-id [id]
  (sql/query *db* ["SELECT * FROM posts where id = ? " id]
             {:result-set-fn first}))

(defn publish-post! [id]
  (sql/update! *db* :posts {:publish 1} ["id = ?" id]))

(defn get-posts-publish []
  (sql/query *db*
             ["select * from posts where publish = 1 order by create_time desc"]))


(defn delete-post! [id]
  (sql/delete! *db* :posts ["id = ?" id]))
