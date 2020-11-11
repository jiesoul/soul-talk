(ns soul-talk.app-key.handler
  (:require [buddy.auth.backends.token :refer [token-backend]]
            [soul-talk.app-key.db :as db]
            [soul-talk.utils :as utils]
            [taoensso.timbre :as log]
            [soul-talk.pagination :as p]))

(defn auth-app-key [token]
  (db/auth-app-key token))

(defn save-app-key [app-key]
  (let [app-key (db/save-app-key app-key)]
    (utils/ok "保存成功" {:app-key app-key})))

(defn gen-app-key []
  (let [token (utils/gen-token)]
    (utils/ok "生成成功" {:token token})))

(defn delete-app-key [id]
  (let [result (db/delete-app-key id)]
    (utils/ok "删除成功")))

(defn load-app-keys [req]
  (let [query-params (:query-params req)
        pagination (p/create query-params)
        keys (db/load-app-keys pagination query-params)
        total (db/count-app-keys query-params)]
    (utils/ok "获取成功" {:app-keys keys
                      :pagination pagination
                      :query-str query-params})))

(defn query-app-key [req]
  (let [query-str (:query-params req)]
    (log/info "query params: " query-str)
    (utils/ok {:query-str query-str})))