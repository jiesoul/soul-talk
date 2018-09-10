(ns soul-talk.models.category-db
  (:require [soul-talk.models.db :refer [db-spec]]
            [clojure.java.jdbc :as sql]))

(defn save-category! [category]
  (sql/insert! db-spec :categories category))

(defn get-categories []
  (sql/query db-spec ["select * from categories"]))

(defn delete-category! [id]
  (sql/delete! db-spec :categories ["id = ?" id]))
