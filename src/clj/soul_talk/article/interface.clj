(ns soul-talk.article.interface
  (:require [soul-talk.article.spec :as spec]
            [soul-talk.article.handler :as handler]))

(def create-article spec/create-article)
(def update-article spec/update-article)

(defn get-publish-article
  [req]
  (handler/get-publish-article req))

(defn get-article-archives
  []
  (handler/get-article-archives))

(defn get-article-archives-year-month
  [year month]
  (handler/get-article-archives-year-month year month))

(defn get-article [id]
  (handler/get-article id))

(defn get-all-articles
  [req]
  (handler/get-all-articles [req]))

(defn insert-article!
  [article]
  (handler/insert-article! article))

(defn update-article!
  [article]
  (handler/update-article! article))

(defn delete-article!
  [id]
  (handler/delete-article! id))

(defn publish-article!
  [id]
  (handler/publish-article! id))

(defn upload-article!
  [req]
  (handler/update-article! req))