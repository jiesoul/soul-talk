(ns soul-talk.models.category-db
  (:require [soul-talk.models.db :refer [*db*]]
            [clojure.java.jdbc :as sql]))

(defn save-category! [category]
  (sql/insert! *db* :categories category))

(defn get-categories []
  (sql/query *db* ["select * from categories"]))

(defn delete-category! [id]
  (sql/delete! *db* :categories ["id = ?" id]))
