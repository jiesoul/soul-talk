(ns soul-talk.tag.db
  (:require [soul-talk.database.db :refer [*db* coll-to-in-str]]
            [next.jdbc.result-set :as rs-set]
            [next.jdbc.sql :as sql]
            [clojure.tools.logging :as log]))

(defn save-tag! [tag]
  (sql/insert! *db* :tag tag
    {:builder-fn rs-set/as-unqualified-maps}))

(defn delete-tag! [id]
  (sql/delete! *db* :tag ["id = ?" id]))

(defn gen-where [{:keys [name]}]
  (let [where-str (str "where name like ?")
        coll [(str "%" name "%")]]
    [where-str coll]))

(defn load-tags-page [{:keys [per_page offset]} params]
  (let [where   (gen-where params)
        sql-str (str "select * from tag " (first where) " offset ? limit ?")
        tags
                (sql/query *db*
                  (into [sql-str] (conj (second where) offset per_page))
                  {:builder-fn rs-set/as-unqualified-maps})
        count-str (str "select count(1) as c from tag " (first where))
        total   (:c
                  (first
                    (sql/query *db*
                      (into [count-str] (second where)))))]
    [tags total]))

(defn get-tag-by-id [id]
  (sql/get-by-id *db* :tag id))

(defn get-tag-by-name [name]
  (first
    (sql/find-by-keys  *db* :tag ["name = ?" name]
      {:builder-fn rs-set/as-unqualified-maps})))

(defn tags-with-names [tag-names]
  (let [tag-str (coll-to-in-str tag-names)]
    (sql/query *db*
      [(str "select * from tag where name in (" tag-str ")")]
      {:builder-fn rs-set/as-unqualified-maps})))


(defn get-tag-by-article-id [id]
  (sql/query *db*
    ["select * from tag where id in (select tag_id from article_tag where article_id = ?)" id]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn query-tags [name]
  (sql/query *db*
    ["select * from tag where name like ?" (str "%" name "%")]
    {:builder-fn rs-set/as-unqualified-maps}))


