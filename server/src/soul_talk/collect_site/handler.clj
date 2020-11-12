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

