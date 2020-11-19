(ns soul-talk.collect-site.handler
  (:require [buddy.auth.backends.token :refer [token-backend]]
            [soul-talk.collect-site.db :as db]
            [soul-talk.utils :as utils]
            [taoensso.timbre :as log]
            [soul-talk.pagination :as p]
            [java-time.local :as l]
            [soul-talk.collect-site.spec :as spec]))

(def create-collect-site spec/create-collect-site)
(def update-collect-site spec/update-collect-site)
(def collect-site-tag spec/collect-site-tag)
(def collect-site-series spec/collect-site-series)
(def collect-site-comment spec/collect-site-comment)

(defn save-collect-site [collect-site]
  (let [now (l/local-date-time)
        collect-site (db/save-collect-site (assoc collect-site :create_at now :update_at now))]
    (utils/ok "保存成功" {:collect-site collect-site})))

(defn update-collect-site [collect-site]
  (let [result (db/update-collect-site (assoc collect-site :update_at (l/local-date-time)))]
    (utils/ok "更新成功")))

(defn get-collect-site [id]
  (let [collect-site (db/get-collect-site id)]
    (utils/ok {:collect-site collect-site})))

(defn delete-collect-site [id]
  (let [result (db/delete-collect-site id)]
    (utils/ok "删除成功")))

(defn load-collect-sites-page [req]
  (let [query-params (:query-params req)
        pagination (p/create query-params)
        [keys total] (db/load-collect-sites-page pagination query-params)
        pagination (p/create-total pagination total)]
    (utils/ok "获取成功" {:collect-sites keys
                      :pagination pagination
                      :query-str query-params})))

(defn query-collect-site [req]
  (let [query-str (:query-params req)]
    (utils/ok {:query-str query-str})))


(defn save-collect-site-tag! [collect-site-tag]
  (let [collect-site-tag (db/save-collect-site-tag! collect-site-tag)]
    (utils/ok "保存成功" {:collect-site-tag collect-site-tag})))

(defn save-collect-sites-tags! [collect-sites-tags]
  (let [collect-site-tags (db/save-collect-sites-tags! collect-sites-tags)]
    (utils/ok "保存成功" {:collect-site-tags collect-site-tags})))

(defn get-collect-site-tags [collect-site-id]
  (let [collect-site-tags (db/get-collect-sites-tags-by-collect-site-id collect-site-id)]))

(defn delete-collect-site-tag-by-collect-site-id! [collect-site-id]
  (let [rs (db/delete-collect-site-tag-by-collect-site-id collect-site-id)]))

(defn delete-collect-site-tag-by-id! [id]
  (let [rs (db/delete-collect-site-tag-by-id id)]
    (utils/ok "删除成功")))

(defn save-collect-site-series! [collect-site-series]
  (let [collect-site-series (db/save-collect-site-series! collect-site-series)]
    (utils/ok "保存成功" {:collect-site-series collect-site-series})))

(defn save-collect-site-series-all! [collect-site-series-all]
  (let [collect-site-series-all (db/save-collect-sites-series-all! collect-site-series-all)]
    (utils/ok "保存成功" {:collect-site-series collect-site-series-all})))

(defn get-collect-site-series [collect-site-id]
  (let [collect-site-series (db/get-collect-site-series-by-collect-site-id collect-site-id)]))

(defn delete-collect-site-series-by-collect-site-id! [collect-site-id]
  (let [rs (db/delete-collect-site-series-by-collect-site-id collect-site-id)]))

(defn delete-collect-site-series-by-id! [id]
  (let [rs (db/delete-collect-site-series-by-id id)]
    (utils/ok "删除成功")))

(defn save-collect-site-comment! [collect-site-comment]
  (let [collect-site-comment (db/save-collect-site-comment! collect-site-comment)]
    (utils/ok {:comment collect-site-comment})))

(defn get-comments-by-collect-siteId [collect-siteId]
  (let [comments (db/get-comments-by-collect-site-id collect-siteId)]
    (utils/ok "获取成功" {:comments comments})))

(defn load-collect-sites-comments-page [req]
  (let [params (:params req)
        pagination (p/create req)
        [comments total] (db/load-collect-sites-comments-page pagination params)
        pagination (p/create-total pagination total)]
    (utils/ok {:comments comments
               :pagination pagination})))

(defn delete-collect-site-comment-by-id! [id]
  (let [rs (db/delete-collect-site-comment-by-id! id)]
    (utils/ok "删除成功")))

(defn delete-collect-site-comments-by-collect-site-id! [collect-site-id]
  (let [rs (db/delete-collect-sites-comments-by-collect-site-id! collect-site-id)]
    (utils/ok "删除成功")))

