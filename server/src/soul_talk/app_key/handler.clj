(ns soul-talk.app-key.handler
  (:require [buddy.auth.backends.token :refer [token-backend]]
            [soul-talk.app-key.db :as db]
            [soul-talk.utils :as utils]
            [taoensso.timbre :as log]
            [soul-talk.pagination :as p]
            [soul-talk.app-key.spec :as spec]))

(def token spec/token)
(def create-app-key spec/create-app-key)
(def update-app-key spec/update-app-key)

(defn auth-app-key [token]
  (db/auth-app-key token))

(defn save-app-key! [{:keys [app_name] :as app-key}]
  (let [app-key (db/save-app-key! app-key)]
    (utils/ok {:app-key app-key})))

(defn gen-app-key []
  (let [token (utils/gen-token)]
    (utils/ok {:token token})))

(defn get-app-key [id]
  (let [app-key (db/get-app-key id)]
    (utils/ok {:app-key app-key})))

(defn update-app-key! [app-key]
  (let [_ (db/update-app-key! app-key)]
    (utils/ok "保存成功")))

(defn delete-app-key! [id]
  (let [result (db/delete-app-key! id)]
    (utils/ok "删除成功")))

(defn load-app-keys-page [req]
  (let [params (:params req)
        pagination (p/create req)
        [keys total] (db/load-app-keys-page pagination params)
        pagination (p/create-total pagination total)]
    (utils/ok "获取成功" {:app-keys keys
                         :pagination pagination
                         :query-params params})))

(defn query-app-key [req]
  (let [query-str (:query-params req)]
    (utils/ok {:query-params query-str})))