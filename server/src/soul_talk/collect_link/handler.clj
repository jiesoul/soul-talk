(ns soul-talk.collect-link.handler
  (:require [buddy.auth.backends.token :refer [token-backend]]
            [soul-talk.collect-link.db :as db]
            [soul-talk.utils :as utils]
            [taoensso.timbre :as log]
            [soul-talk.pagination :as p]))

(defn auth-collect-link [token]
  (db/auth-collect-link token))

(defn save-collect-link [collect-link]
  (let [collect-link (db/save-collect-link collect-link)]
    (utils/ok "保存成功" {:collect-link collect-link})))

(defn gen-collect-link []
  (let [token (utils/gen-token)]
    (utils/ok "生成成功" {:token token})))

(defn delete-collect-link [id]
  (let [result (db/delete-collect-link id)]
    (utils/ok "删除成功")))

(defn load-collect-links [req]
  (let [query-params (:query-params req)
        pagination (p/create query-params)
        keys (db/load-collect-links pagination query-params)
        total (db/count-collect-links query-params)]
    (utils/ok "获取成功" {:collect-links keys
                      :pagination pagination
                      :query-str query-params})))

(defn query-collect-link [req]
  (let [query-str (:query-params req)]
    (log/info "query params: " query-str)
    (utils/ok {:query-str query-str})))