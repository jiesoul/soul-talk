(ns soul-talk.api-key.handler
  (:require [buddy.auth.backends.token :refer [token-backend]]
            [soul-talk.api-key.db :as db]
            [soul-talk.utils :as utils]))

(def auth-api-key (token-backend {:authfn       db/auth-api-key
                                  :unauthorized utils/unauthorized}))

(defn save-api-key [api-key]
  (let [api-key (db/save-api-key api-key)]
    (utils/ok "保存成功" {:api-key api-key})))

(defn gen-api-key []
  (let [token (utils/gen-token)]
    (utils/ok "生成成功" {:token token})))

(defn delete-api-key [token]
  (let [result (db/delete-api-key token)]
    (utils/ok "删除成功")))

(defn load-all-api-keys []
  (let [keys (db/load-all-api-keys)]
    (utils/ok {:api-keys keys})))