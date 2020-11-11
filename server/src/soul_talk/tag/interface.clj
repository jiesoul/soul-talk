(ns soul-talk.tag.interface
  (:require [soul-talk.tag.handler :as handler]
            [soul-talk.tag.spec :as spec]))

(def tag spec/tag)

(defn insert-tag! [tag]
  (handler/insert-tag! tag))

(defn delete-tag! [id]
  (handler/delete-tag! id))

(defn get-tag-by-id [id]
  (handler/get-tag-by-id id))

(defn get-tag-by-name [name]
  (handler/get-tag-by-name name))

(defn get-all-tags []
  (handler/get-all-tags))

(defn get-tags-by-article-id [id]
  (handler/get-tags-by-article-id id))

(defn query-tags [name]
  (handler/query-tags name))