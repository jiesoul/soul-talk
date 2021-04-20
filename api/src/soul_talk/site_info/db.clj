(ns soul-talk.site-info.db
  (:require [soul-talk.database.db :refer [*db*]]
            [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as rs-set]))

(defn update! [{:keys [id] :as site-info}]
  (sql/update! *db* :site_info site-info {:id id}))

(defn get-by-id [id]
  (sql/get-by-id *db* :site_info id))
