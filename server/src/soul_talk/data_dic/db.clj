(ns soul-talk.data-dic.db
  (:require [soul-talk.database.db :refer [*db*]]
            [next.jdbc.result-set :as rs-set]
            [next.jdbc.sql :as sql]))

(defn load-all []
  (sql/query *db* "select * from data_dic" {:builder-fn rs-set/as-unqualified-maps}))
