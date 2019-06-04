(ns soul-talk.models.auth-model
  (:require [buddy.auth.backends.token :refer [token-backend]]
            [buddy.auth.accessrules :refer [success error]]
            [buddy.auth :refer [authenticated?]]
            [crypto.random :refer [base64]]
            [soul-talk.models.user-db :as users]
            [soul-talk.models.db :refer [*db*]]
            [clojure.java.jdbc :as sql]
            [taoensso.timbre :as log]))

;;生成Token
(defn gen-session-id
  []
  (base64 32))

;; 插入到数据库
(defn make-token!
  [user-id]
  (let [token (gen-session-id)]
    (sql/insert! *db* :auth_tokens {:id      token
                                    :user_id user-id})))
;; 验证函数
(defn authenticate-token
  [req token]
  (let [sql-str (str "SELECT * FROM auth_tokens "
                  " WHERE id = ?")
        tokens  (sql/query *db* [sql-str token])]
    (log/debug "Token: " token)
    (some-> tokens
      first
      :user_id
      users/find-by-id)))

;; 未通过验证的函数
(defn unauthorized-handler [req msg]
  {:status 401
   :body   {:status  :error
            :message (or msg "User not authorized")}})

;; 验证配置
(def auth-backend (token-backend {:authfn       authenticate-token
                                  :unauthorized unauthorized-handler}))
;; 验证方法
(defn authenticated [req]
  (authenticated? req))

;; api 验证方法
(defn admin [req]
  (authenticated? req))