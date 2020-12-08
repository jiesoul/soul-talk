(ns soul-talk.collect-link.handler
  (:require [buddy.auth.backends.token :refer [token-backend]]
            [soul-talk.collect-link.db :as db]
            [soul-talk.utils :as utils]
            [taoensso.timbre :as log]
            [java-time.local :as l]
            [soul-talk.pagination :as p]
            [soul-talk.collect-link.spec :as spec]))

(def create-collect-link spec/create-collect-link)
(def update-collect-link spec/update-collect-link)
(def collect-link-tag spec/collect-link-tag)
(def collect-link-series spec/collect-link-series)
(def collect-link-comment spec/collect-link-comment)

(defn save-collect-link [collect-link]
  (let [collect-link (db/save-collect-link collect-link)]
    (utils/ok "保存成功" {:collect-link collect-link})))

(defn get-collect-link [id]
  (let [collect-link (db/get-collect-link id)]
    (utils/ok {:collect-link collect-link})))

(defn delete-collect-link [id]
  (let [result (db/delete-collect-link id)]
    (utils/ok "删除成功")))

(defn load-collect-links-page [req]
  (let [params (:query-params req)
        pagination (p/create req)
        [collect-links total] (db/load-collect-links-page pagination params)
        pagination (p/create-total pagination total)]
    (utils/ok "获取成功" {:collect-links collect-links
                      :pagination pagination
                      :query-params params})))

(defn update-collect-link [collect-link]
  (let [collect-link (db/update-collect-link (assoc collect-link :update_at (l/local-date-time)))]
    (utils/ok "更新成功")))


(defn save-collect-link-tag! [collect-link-tag]
  (let [collect-link-tag (db/save-collect-link-tag! collect-link-tag)]
    (utils/ok "保存成功" {:collect-link-tag collect-link-tag})))

(defn save-collect-link-tags! [collect-link-tags]
  (let [collect-link-tags (db/save-collect-link-tags! collect-link-tag)]
    (utils/ok "保存成功" {:collect-link-tags collect-link-tags})))

(defn get-collect-link-tags [collect-link-id]
  (let [collect-link-tags (db/get-collect-link-tags-by-collect-link-id collect-link-id)]))

(defn delete-collect-link-tag-by-collect-link-id! [collect-link-id]
  (let [rs (db/delete-collect-link-tag-by-collect-link-id collect-link-id)]))

(defn delete-collect-link-tag-by-id! [id]
  (let [rs (db/delete-collect-link-tag-by-id id)]
    (utils/ok "删除成功")))

(defn save-collect-link-series! [collect-link-series]
  (let [collect-link-series (db/save-collect-link-series! collect-link-series)]
    (utils/ok "保存成功" {:collect-link-series collect-link-series})))

(defn save-collect-link-series-all! [collect-link-series-all]
  (let [collect-link-series-all (db/save-collect-links-series-all! collect-link-series-all)]
    (utils/ok "保存成功" {:collect-link-series collect-link-series-all})))

(defn get-collect-link-series [collect-link-id]
  (let [collect-link-series (db/get-collect-link-series-by-collect-link-id collect-link-id)]))

(defn delete-collect-link-series-by-collect-link-id! [collect-link-id]
  (let [rs (db/delete-collect-link-series-by-collect-link-id collect-link-id)]))

(defn delete-collect-link-series-by-id! [id]
  (let [rs (db/delete-collect-link-series-by-id id)]
    (utils/ok "删除成功")))

(defn save-collect-link-comment! [collect-link-comment]
  (let [collect-link-comment (db/save-collect-link-comment! collect-link-comment)]
    (utils/ok {:comment collect-link-comment})))

(defn get-comments-by-collect-link-id [collect-linkId]
  (let [comments (db/get-comments-by-collect-link-id collect-linkId)]
    (utils/ok "获取成功" {:comments comments})))

(defn load-collect-links-comments-page [req]
  (let [params (:params req)
        pagination (p/create req)
        [comments total] (db/load-collect-links-comments-page pagination params)
        pagination (p/create-total pagination total)]
    (utils/ok {:comments comments
               :pagination pagination})))

(defn delete-collect-link-comment-by-id! [id]
  (let [rs (db/delete-collect-link-comment-by-id! id)]
    (utils/ok "删除成功")))

(defn delete-collect-link-comments-by-collect-link-id! [collect-link-id]
  (let [rs (db/delete-collect-link-comments-by-collect-link-id! collect-link-id)]
    (utils/ok "删除成功")))

