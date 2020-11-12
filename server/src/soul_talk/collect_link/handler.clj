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
                      :query-str params})))

(defn update-collect-link [collect-link]
  (let [collect-link (db/update-collect-link (assoc collect-link :update_at (l/local-date-time)))]
    (utils/ok "更新成功")))
