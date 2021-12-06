(ns soul-talk.reply.db
  (:require [next.jdbc.sql :as sql]
            [soul-talk.database.db :refer [*db*]]
            [next.jdbc.result-set :as rs-set]
            [cambium.core :as log]
            [clojure.string :as str]))

(defn insert-reply! [reply]
  (sql/insert! *db* :reply reply))

(defn gen-where [{:keys [title publish]}]
  (let [[where-str coll] [(str "where title like ?")
                          [(str "%" title "%")]]
        [where-str coll] (if (str/blank? publish)
                           [where-str coll]
                           [(str where-str " and publish = ?") (conj coll publish)])]
    [where-str coll]))

(defn load-replies-page [{:keys [offset per-page]} params]
  (let [[where coll] (gen-where params)
        query-str (str "select * from reply " where " order by create_at desc offset ? limit ?")
        replies (sql/query *db*
                   (into [query-str] (conj coll offset per-page)))
        count-str (str "select count(1) as c from reply " where)
        total (:c
                (first
                  (sql/query *db*
                    (into [count-str] coll))))]
    [replies total]))

(defn get-reply-by-id [id]
  (sql/get-by-id *db* :reply id))

(defn delete-reply! [id]
  (sql/delete! *db* :reply ["id = ?" id]))










