(ns soul-talk.models.category-db
  (:require [soul-talk.models.db :refer [*db*]]
            [clojure.java.jdbc :as sql]
            [taoensso.timbre :as log]))

(defn save-category! [category]
  (sql/insert! *db* :categories category))

(defn update-category! [{:keys [name id]}]
  (sql/update! *db* :categories {:name name} ["id = ?" id]))

(defn get-categories []
  (sql/query *db* ["select * from categories"]))

(defn get-category-by-id [id]
  (first
    (sql/query *db* ["select * from categories where id = ?" id])))

(defn get-category-by-name [name]
  (first
    (sql/query *db* ["select * from categories where name = ?" name])))

(defn delete-category! [id]
  (sql/delete! *db* :categories ["id = ?" id]))
