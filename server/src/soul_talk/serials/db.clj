(ns soul-talk.serials.db
  (:require [soul-talk.database.db :refer [*db*]]
            [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as rs-set]))

(defn load-serials []
  (sql/query *db*
    ["select * from serials"]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn save-serials [serials]
  (sql/insert! *db* :serials serials {:builder-fn rs-set/as-unqualified-maps}))

(defn update-serials [{:keys [id] :as serials}]
  (sql/update! *db* :serials serials {:id id}))

(defn delete-serials [id]
  (sql/delete! *db* :serials [:id id]))
