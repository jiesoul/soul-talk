(ns soul-talk.reply.handler
  (:require [soul-talk.reply.db :as reply-db]
            [soul-talk.utils :as utils]
            [java-time.local :as l]
            [java-time.format :as f]
            [soul-talk.pagination :as p]
            [soul-talk.reply.spec :as spec]))

(def create-reply spec/create-reply)
(def update-reply spec/update-reply)
(def reply-tag spec/reply-tag)
(def reply-category spec/reply-category)
(def reply-comment spec/reply-comment)

(def format-id (java-time.format/formatter "yyyyMMddHHmmssSSS"))

(def publish-false "1102")
(def publish-true "1101")

(defn load-replies-page [req]
  (let [params (:params req)
        pagination (p/create req)
        [replies total] (reply-db/load-replies-page pagination params)
        pagination (p/create-total pagination total)]
    (utils/ok  {:pagination pagination
                :replies   replies
                :query-params params})))

(defn load-replies-publish-page [req]
  (let [pagination (p/create req)
        params (:params req)
        [replies total] (reply-db/load-replies-page pagination (assoc params :publish publish-true))
        pagination (p/create-total pagination total)]
    (utils/ok {:replies   replies
               :pagination pagination
               :query-params params})))

(defn get-reply [reply-id]
  (let [reply (reply-db/get-reply-by-id reply-id)
        reply-tags (reply-db/get-reply-tags-by-reply-id reply-id)]
    (utils/ok "加载成功" {:reply (assoc reply :tags reply-tags)})))

(defn insert-reply! [reply]
  (let [time (l/local-date-time)
        id (f/format format-id time)]
    (let [reply (reply-db/save-reply!
                    (assoc reply :id id :create_at time :update_at time :publish publish-false))]
      (utils/ok {:reply reply}))))

(defn upload-reply! [{:keys [body params] :as req}]
  (let [file (:file params)
        md (slurp (:tempfile file))
        time (l/local-date-time)
        id (f/format format-id time)
        reply {:id id :content md :create_at time :update_at time :title "" :author "" :publish publish-false}]
    (do
      (reply-db/insert-reply! reply)
      (utils/ok "上传成功" {:id id}))))

(defn update-reply! [reply]
  (reply-db/update-reply! (assoc reply :update_at (utils/now) :publish publish-false))
  (utils/ok {:reply (assoc reply :body nil)}))

(defn delete-reply! [id]
  (do
    (reply-db/delete-reply! id)
    (utils/ok "删除成功")))

(defn publish-reply! [id]
  (let [reply (reply-db/get-reply-by-id id)]
    (if reply
      (let [reply (assoc reply :publish publish-true :update_at (utils/now))
            reply (reply-db/publish-reply! reply)]
        (utils/ok "发布成功" {:reply reply}))
      (utils/bad-request "未找到文章"))))

(defn get-reply-archives []
  (let [archives (reply-db/get-reply-archives)]
    (utils/ok "获取成功" {:archives archives})))

(defn get-reply-archives-year-month [year month]
  (let [reply (reply-db/get-reply-archives-year-month year month)]
    (utils/ok "获取成功" {:reply reply})))

(defn get-reply-public [id]
  (let [reply (reply-db/get-reply-publish id)]
    (utils/ok {:reply reply})))

(defn save-reply-tag! [reply-tag]
  (let [reply-tag (reply-db/save-reply-tag! reply-tag)]
    (utils/ok "保存成功" {:reply-tag reply-tag})))

(defn save-reply-tags! [reply-tags]
  (let [reply-tags (reply-db/save-reply-tags! reply-tag)]
    (utils/ok "保存成功" {:reply-tags reply-tags})))

(defn get-reply-tags [reply-id]
  (let [reply-tags (reply-db/get-reply-tags-by-reply-id reply-id)]))

(defn delete-reply-tag-by-reply-id! [reply-id]
  (let [rs (reply-db/delete-reply-tag-by-reply-id reply-id)]))

(defn delete-reply-tag-by-id! [id]
  (let [rs (reply-db/delete-reply-tag-by-id id)]
    (utils/ok "删除成功")))

(defn save-reply-category! [reply-category]
  (let [reply-category (reply-db/save-reply-category! reply-category)]
    (utils/ok "保存成功" {:reply-category reply-category})))

(defn save-reply-category-all! [reply-category-all]
  (let [reply-category-all (reply-db/save-reply-category-all! reply-category-all)]
    (utils/ok "保存成功" {:reply-category reply-category-all})))

(defn get-reply-category [reply-id]
  (let [reply-category (reply-db/get-reply-category-by-reply-id reply-id)]))

(defn delete-reply-category-by-reply-id! [reply-id]
  (let [rs (reply-db/delete-reply-category-by-reply-id reply-id)]))

(defn delete-reply-category-by-id! [id]
  (let [rs (reply-db/delete-reply-category-by-id id)]
    (utils/ok "删除成功")))

(defn save-reply-comment! [reply-comment]
  (let [reply-comment (reply-db/save-reply-comment! reply-comment)]
    (utils/ok {:comment reply-comment})))

(defn get-comments-by-replyId [replyId]
  (let [comments (reply-db/get-comments-by-replyId replyId)]
    (utils/ok "获取成功" {:comments comments})))

(defn load-replies-comments-page [req]
  (let [params (:params req)
        pagination (p/create req)
        [comments total] (reply-db/load-replies-comments-page pagination params)
        pagination (p/create-total pagination total)]
    (utils/ok {:comments comments
               :pagination pagination})))

(defn delete-reply-comment-by-id! [id]
  (let [rs (reply-db/delete-reply-comment-by-id! id)]
    (utils/ok "删除成功")))

(defn delete-reply-comments-by-reply-id! [reply-id]
  (let [rs (reply-db/delete-reply-comments-by-reply-id! reply-id)]
    (utils/ok "删除成功")))





