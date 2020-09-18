(ns soul-talk.tag.db
  (:require [soul-talk.database.db :refer [*db*]]
            [clojure.java.jdbc :as jdbc]
            [taoensso.timbre :as log]))

(defn save-tag! [tag]
  (jdbc/insert! *db* :tags tag))

(defn delete-tag! [id]
  (jdbc/delete! *db* :tags ["id = ?" id]))

(defn get-tags []
  (jdbc/query *db* ["select * from tags"]))
