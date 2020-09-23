(ns soul-talk.article.handler
  (:require [soul-talk.article.db :as article-db]
            [ring.util.http-response :as resp]
            [java-time.local :as l]
            [java-time.format :as f]
            [soul-talk.pagination :as p]))

(def format-id (java-time.format/formatter "yyyyMMddHHmmssSSS"))

(defn get-all-articles [request]
  (let [pagination (p/create request)
        articles (article-db/get-article-page pagination)
        total (article-db/count-article-all)
        pagination (p/create-total pagination total)]
    (resp/ok {:result :ok
              :pagination pagination
              :articles  articles})))

(defn get-publish-article [req]
  (let [pagination (p/create req)
        article (article-db/get-article-publish-page pagination)
        total (article-db/count-article-publish)
        pagination (p/create-total pagination total)]
    (resp/ok {:result :ok
              :article article
              :pagination pagination})))

(defn get-article [article-id]
  (let [article (article-db/get-article-by-id article-id)]
    (resp/ok {:result :ok
              :article article})))

(defn insert-article! [article]
  (let [time (l/local-date-time)
        id (f/format format-id time)]
    (let [article (article-db/save-article!
                    (-> article
                      (assoc :id id)
                      (assoc :createAt time)
                      (assoc :modifyAt time)))]
      (-> {:result :ok
           :message "保存成功"
           :article article}
        (resp/ok)))))

(defn upload-article! [{:keys [body params] :as req}]
  (let [file (:file params)
        md (slurp (:tempfile file))
        time (l/local-date-time)
        id (f/format format-id time)
        article {:id id :content md :createAt time :modifyAt time :title "" :author "" :publish 0}]
    (do
      (article-db/insert-article! article)
      (-> {:result :ok
           :id id}
        resp/ok))))

(defn update-article! [article]
  (article-db/update-article! (-> article
                                (assoc :modifyAt (l/local-date-time))))
  (-> {:result :ok
       :article   (assoc article :body nil)}
    (resp/ok)))

(defn delete-article! [id]
  (do
    (article-db/delete-article! id)
    (resp/ok {:result :ok
              :message "删除成功"})))

(defn publish-article! [id]
  (do
    (article-db/publish-article! id)
    (-> {:result :ok
         :message "发布成功"}
      (resp/ok))))

(defn get-article-archives []
  (let [archives (article-db/get-article-archives)]
    (-> {:result :ok
         :archives archives}
      resp/ok)))

(defn get-article-archives-year-month [year month]
  (let [article (article-db/get-article-archives-year-month year month)]
    (-> {:result :ok
         :article article}
      resp/ok)))