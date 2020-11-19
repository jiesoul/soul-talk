(ns soul-talk.tag.handler
  (:require [soul-talk.tag.db :as tag-db]
            [soul-talk.utils :as utils]
            [soul-talk.pagination :as p]
            [soul-talk.tag.spec :as spec]))

(def tag spec/tag)

(defn load-tags-page [req]
  (let [pagination (p/create req)
        params (:params req)
        tags (tag-db/load-tags-page pagination params)
        pagination (p/create-total pagination (second tags))]
    (utils/ok {:tags (first tags)
               :pagination pagination
               :query-str params})))

(defn save-tag! [{:keys [name] :as tag}]
  (if-let [t (tag-db/get-tag-by-name name)]
    (utils/bad-request (str "<" name "> 标签已经存在"))
    (let [t (tag-db/save-tag! tag)]
      (utils/ok {:tag t}))))

(defn delete-tag! [id]
  (let [m (tag-db/delete-tag! id)]
    (utils/ok "删除成功")))

(defn get-tag-by-id [id]
  (let [tag (tag-db/get-tag-by-id id)]
    (utils/ok {:tag tag})))

(defn get-tag-by-name [name]
  (let [tag (tag-db/get-tag-by-name name)]
    (utils/ok {:tag tag})))

(defn get-tags-by-article-id [id]
  (let [tags (tag-db/get-tag-by-article-id id)]
    (utils/ok {:tags tags})))

(defn query-tags [name]
  (let [tags (tag-db/query-tags name)]
    (utils/ok {:tags tags})))