(ns soul-talk.article.handler
  (:require [soul-talk.article.db :as article-db]
            [soul-talk.utils :as utils]
            [java-time.local :as l]
            [java-time.format :as f]
            [soul-talk.pagination :as p]
            [soul-talk.article.spec :as spec]))

(def create-article spec/create-article)
(def update-article spec/update-article)
(def create-comment spec/create-comment)

(def format-id (java-time.format/formatter "yyyyMMddHHmmssSSS"))

(defn load-articles-page [request]
  (let [pagination (p/create request)
        params (:param request)
        [articles total] (article-db/load-articles-page pagination params)
        pagination (p/create-total pagination total)]
    (utils/ok  {:pagination pagination
                :articles   articles
                :query-str params})))

(defn load-articles-publish-page [req]
  (let [pagination (p/create req)
        params (:params req)
        [articles total] (article-db/load-articles-page pagination (assoc params :public 1))
        pagination (p/create-total pagination total)]
    (utils/ok {:articles   articles
               :pagination pagination
               :query-str params})))

(defn get-article [article-id]
  (let [article (article-db/get-article-by-id article-id)]
    (utils/ok "加载成功" {:article article})))

(defn insert-article! [article]
  (let [time (l/local-date-time)
        id (f/format format-id time)]
    (let [article (article-db/save-article!
                    (-> article
                      (assoc :id id)
                      (assoc :create_at time)
                      (assoc :update_at time)))]
      (utils/ok "保存成功" {:article article}))))

(defn upload-article! [{:keys [body params] :as req}]
  (let [file (:file params)
        md (slurp (:tempfile file))
        time (l/local-date-time)
        id (f/format format-id time)
        article {:id id :content md :create_at time :update_at time :title "" :author "" :publish 0}]
    (do
      (article-db/insert-article! article)
      (utils/ok "上传成功" {:id id}))))

(defn update-article! [article]
  (article-db/update-article! (-> article
                                (assoc :update_at (l/local-date-time))))
  (utils/ok {:article (assoc article :body nil)}))

(defn delete-article! [id]
  (do
    (article-db/delete-article! id)
    (utils/ok "删除成功")))

(defn publish-article! [id]
  (do
    (article-db/publish-article! id)
    (utils/ok "发布成功")))

(defn get-article-archives []
  (let [archives (article-db/get-article-archives)]
    (utils/ok "获取成功" {:archives archives})))

(defn get-article-archives-year-month [year month]
  (let [article (article-db/get-article-archives-year-month year month)]
    (utils/ok "获取成功" {:article article})))


(defn get-comments-by-articleId [articleId]
  (let [comments (article-db/get-comments-by-articleId articleId)]
    (utils/ok "获取成功" {:comments comments})))

(defn get-article-public [id]
  (let [article (article-db/get-article-publish id)]
    (utils/ok {:article article})))

(defn save-article-comment [id comment]
  (let [comment (article-db/save-comment! comment)]
    (utils/ok {:comment comment})))

(defn delete-article-comment [id]
  (article-db/delete-comment! id))

(defn load-articles-comments-page [req]
  (let [params (:params req)
        pagination (p/create req)
        [comments total] (article-db/load-articles-comments-page pagination params)
        pagination (p/create-total pagination total)]
    (utils/ok {:comments comments
               :pagination pagination})))

