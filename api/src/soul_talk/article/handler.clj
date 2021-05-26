(ns soul-talk.article.handler
  (:require [soul-talk.article.db :as article-db]
            [soul-talk.utils :as utils]
            [java-time.local :as l]
            [java-time.format :as f]
            [soul-talk.pagination :as p]
            [soul-talk.article.spec :as spec]))

(def create-article spec/create-article)
(def update-article spec/update-article)
(def article-tag spec/article-tag)
(def article-category spec/article-category)
(def article-comment spec/article-comment)

(def format-id (java-time.format/formatter "yyyyMMddHHmmssSSS"))

(def publish-false "1102")
(def publish-true "1101")

(defn load-articles-page [req]
  (let [params (:params req)
        pagination (p/create req)
        [articles total] (article-db/load-articles-page pagination params)
        pagination (p/create-total pagination total)]
    (utils/ok  {:pagination pagination
                :articles   articles
                :query-params params})))

(defn load-articles-publish-page [req]
  (let [pagination (p/create req)
        params (:params req)
        [articles total] (article-db/load-articles-page pagination (assoc params :publish publish-true))
        pagination (p/create-total pagination total)]
    (utils/ok {:articles   articles
               :pagination pagination
               :query-params params})))

(defn get-article [article-id]
  (let [article (article-db/get-article-by-id article-id)
        article-tags (article-db/get-article-tags-by-article-id article-id)]
    (utils/ok "加载成功" {:article (assoc article :tags article-tags)})))

(defn insert-article! [article]
  (let [time (l/local-date-time)
        id (f/format format-id time)]
    (let [article (article-db/save-article!
                    (assoc article :id id :create_at time :update_at time :publish publish-false))]
      (utils/ok {:article article}))))

(defn upload-article! [{:keys [body params] :as req}]
  (let [file (:file params)
        md (slurp (:tempfile file))
        time (l/local-date-time)
        id (f/format format-id time)
        article {:id id :content md :create_at time :update_at time :title "" :author "" :publish publish-false}]
    (do
      (article-db/insert-article! article)
      (utils/ok "上传成功" {:id id}))))

(defn update-article! [article]
  (article-db/update-article! (assoc article :update_at (utils/now) :publish publish-false))
  (utils/ok {:article (assoc article :body nil)}))

(defn delete-article! [id]
  (do
    (article-db/delete-article! id)
    (utils/ok "删除成功")))

(defn publish-article! [id]
  (let [article (article-db/get-article-by-id id)]
    (if article
      (let [article (assoc article :publish publish-true :update_at (utils/now))
            article (article-db/publish-article! article)]
        (utils/ok "发布成功" {:article article}))
      (utils/bad-request "未找到文章"))))

(defn get-article-archives []
  (let [archives (article-db/get-article-archives)]
    (utils/ok "获取成功" {:archives archives})))

(defn get-article-archives-year-month [year month]
  (let [article (article-db/get-article-archives-year-month year month)]
    (utils/ok "获取成功" {:article article})))

(defn get-article-public [id]
  (let [article (article-db/get-article-publish id)]
    (utils/ok {:article article})))

(defn save-article-tag! [article-tag]
  (let [article-tag (article-db/save-article-tag! article-tag)]
    (utils/ok "保存成功" {:article-tag article-tag})))

(defn save-article-tags! [article-tags]
  (let [article-tags (article-db/save-article-tags! article-tag)]
    (utils/ok "保存成功" {:article-tags article-tags})))

(defn get-article-tags [article-id]
  (let [article-tags (article-db/get-article-tags-by-article-id article-id)]))

(defn delete-article-tag-by-article-id! [article-id]
  (let [rs (article-db/delete-article-tag-by-article-id article-id)]))

(defn delete-article-tag-by-id! [id]
  (let [rs (article-db/delete-article-tag-by-id id)]
    (utils/ok "删除成功")))

(defn save-article-category! [article-category]
  (let [article-category (article-db/save-article-category! article-category)]
    (utils/ok "保存成功" {:article-category article-category})))

(defn save-article-category-all! [article-category-all]
  (let [article-category-all (article-db/save-article-category-all! article-category-all)]
    (utils/ok "保存成功" {:article-category article-category-all})))

(defn get-article-category [article-id]
  (let [article-category (article-db/get-article-category-by-article-id article-id)]))

(defn delete-article-category-by-article-id! [article-id]
  (let [rs (article-db/delete-article-category-by-article-id article-id)]))

(defn delete-article-category-by-id! [id]
  (let [rs (article-db/delete-article-category-by-id id)]
    (utils/ok "删除成功")))

(defn save-article-comment! [article-comment]
  (let [article-comment (article-db/save-article-comment! article-comment)]
    (utils/ok {:comment article-comment})))

(defn get-comments-by-articleId [articleId]
  (let [comments (article-db/get-comments-by-articleId articleId)]
    (utils/ok "获取成功" {:comments comments})))

(defn load-articles-comments-page [req]
  (let [params (:params req)
        pagination (p/create req)
        [comments total] (article-db/load-articles-comments-page pagination params)
        pagination (p/create-total pagination total)]
    (utils/ok {:comments comments
               :pagination pagination})))

(defn delete-article-comment-by-id! [id]
  (let [rs (article-db/delete-article-comment-by-id! id)]
    (utils/ok "删除成功")))

(defn delete-article-comments-by-article-id! [article-id]
  (let [rs (article-db/delete-article-comments-by-article-id! article-id)]
    (utils/ok "删除成功")))





