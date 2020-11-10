(ns soul-talk.article.handler
  (:require [soul-talk.article.db :as article-db]
            [soul-talk.utils :as utils]
            [java-time.local :as l]
            [java-time.format :as f]
            [soul-talk.pagination :as p]))

(def format-id (java-time.format/formatter "yyyyMMddHHmmssSSS"))

(defn get-all-articles [request]
  (let [pagination (p/create request)
        articles (article-db/get-article-page pagination)
        total (article-db/count-article-all)
        pagination (p/create-total pagination total)]
    (utils/ok "加载成功" {:pagination pagination
                  :articles   articles})))

(defn get-publish-article [req]
  (let [pagination (p/create req)
        articles (article-db/get-article-publish-page pagination)
        total (article-db/count-article-publish)
        pagination (p/create-total pagination total)]
    (utils/ok {:articles   articles
                 :pagination pagination})))

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

