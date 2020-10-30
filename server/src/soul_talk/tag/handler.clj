(ns soul-talk.tag.handler
  (:require [soul-talk.tag.db :as tag-db]
            [ring.util.http-response :as resp]))

(defn get-all-tags []
  (let [tags (tag-db/get-tags)]
    (resp/ok {:result :ok
              :data   {:tags tags}})))

(defn insert-tag! [{:keys [name] :as tag}]
  (if-let [t (tag-db/get-tag-by-name name)]
    (resp/bad-request {:result  :error
                       :message (str "<" name "> 标签已经存在")})

    (let [t (tag-db/save-tag! tag)]
      (resp/ok {:result :ok
                :data   {:tag t}}))))

(defn delete-tag! [id]
  (let [m (tag-db/delete-tag! id)]
    (resp/ok {:result :ok
              :message "删除成功"})))

(defn get-tag-by-id [id]
  (let [tag (tag-db/get-tag-by-id id)]
    (resp/ok {:result :ok
              :data   {:tag tag}})))

(defn get-tag-by-name [name]
  (let [tag (tag-db/get-tag-by-name name)]
    (resp/ok {:result :ok
              :data   {:tag tag}})))

(defn get-tags-by-article-id [id]
  (let [tags (tag-db/get-tag-by-article-id id)]
    (resp/ok {:result :ok
              :data   {:tags tags}})))