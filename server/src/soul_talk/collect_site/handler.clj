(ns soul-talk.collect-site.handler
  (:require [buddy.auth.backends.token :refer [token-backend]]
            [soul-talk.collect-site.db :as db]
            [soul-talk.utils :as utils]
            [taoensso.timbre :as log]
            [soul-talk.pagination :as p]))

(defn auth-collect-site [token]
  (db/auth-collect-site token))

(defn save-collect-site [collect-site]
  (let [collect-site (db/save-collect-site collect-site)]
    (utils/ok "保存成功" {:collect-site collect-site})))

(defn gen-collect-site []
  (let [token (utils/gen-token)]
    (utils/ok "生成成功" {:token token})))

(defn delete-collect-site [id]
  (let [result (db/delete-collect-site id)]
    (utils/ok "删除成功")))

(defn load-collect-sites [req]
  (let [query-params (:query-params req)
        pagination (p/create query-params)
        keys (db/load-collect-sites pagination query-params)
        total (db/count-collect-sites query-params)]
    (utils/ok "获取成功" {:collect-sites keys
                      :pagination pagination
                      :query-str query-params})))

(defn query-collect-site [req]
  (let [query-str (:query-params req)]
    (log/info "query params: " query-str)
    (utils/ok {:query-str query-str})))