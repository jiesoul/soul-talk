(ns soul-talk.models.tag-db
  (:require [soul-talk.models.db :refer [db-spec]]
            [clojure.java.jdbc :as sql]))

(defn save-tag! [tag]
  (sql/insert! db-spec :tags tag))

(defn delete-tag! [id]
  (sql/delete! db-spec :tags ["id = ?" id]))

(defn get-tags []
  (sql/query db-spec :tags ["select * from tags"]))
