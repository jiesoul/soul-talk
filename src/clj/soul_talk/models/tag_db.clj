(ns soul-talk.models.tag-db
  (:require [soul-talk.models.db :refer [*db*]]
            [clojure.java.jdbc :as sql]
            [taoensso.timbre :as log]))

(defn save-tag! [tag]
  (sql/insert! *db* :tags tag))

(defn delete-tag! [id]
  (sql/delete! *db* :tags ["id = ?" id]))

(defn get-tags []
  (sql/query *db* ["select * from tags"]))
