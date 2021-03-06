(ns soul-talk.models.post-db
  (:require [soul-talk.models.db :refer [*db*]]
            [clojure.java.jdbc :as sql]
            [taoensso.timbre :as log]))

(defn save-post! [post]
  (sql/insert! *db* :posts post))

(defn update-post! [{:keys [id] :as post}]
  (sql/update! *db* :posts post ["id = ?" id]))


(defn get-posts-all []
  (sql/query *db* ["select * from posts order by create_time desc"]))

(defn get-posts-page [{:keys [offset pre-page]}]
  (sql/query *db*
             ["select * from posts order by create_time desc
                offset ? limit ?" offset pre-page]))

(defn count-posts-all []
  (:count
    (first
      (sql/query *db*
                ["select count(id) as count from posts"]))))

(defn get-post-by-id [id]
  (sql/query *db* ["SELECT * FROM posts where id = ? " id]
             {:result-set-fn first}))

(defn publish-post! [id]
  (sql/update! *db* :posts {:publish 1} ["id = ?" id]))

(defn get-posts-publish []
  (sql/query *db*
             ["select * from posts where publish = 1
              order by create_time desc "]))

(defn get-posts-publish-page [{:keys [offset pre-page]}]
  (sql/query *db*
             ["select * from posts where publish = 1
              order by create_time desc offset ? limit ? "
              offset pre-page]))

(defn count-posts-publish []
  (:count
    (first
     (sql/query *db*
                ["select count(id) as count from posts where publish = 1"]))))

(defn delete-post! [id]
  (sql/delete! *db* :posts ["id = ?" id]))

(defn get-posts-archives []
  (sql/query *db*
             ["select year,month, count(month) as counter
                from
                  (select date_part('year', create_time) as year,
                    date_part('month', create_time) as month
                    from posts where publish = 1) t
                group by year,month"]))

(defn get-posts-archives-year-month [year month]
  (sql/query *db*
             ["select * from posts where date_part('year', create_time) = ?
                                      and date_part('month', create_time) = ?
                                      order by create_time desc"
              year month]))

(defn get-post-by-category [category]
  (:counter
    (first
     (sql/query *db* ["select count(id) as counter from posts where category = ?" category]))))
