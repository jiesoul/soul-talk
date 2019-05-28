(ns soul-talk.models.auth-model
  (:require [buddy.auth.backends.token :refer [token-backend]]
            [buddy.auth.accessrules :refer [success error]]
            [buddy.auth :refer [authenticated?]]
            [crypto.random :refer [base64]]
            [soul-talk.models.user-db :as users]
            [soul-talk.models.db :refer [*db*]]
            [clojure.java.jdbc :as sql]
            [taoensso.timbre :as log]))

(defn gen-session-id
  []
  (base64 32))

(defn make-token!
  [user-id]
  (let [token (gen-session-id)]
    (log/debug "token: " token " user-id: " user-id)
    (sql/insert! *db* :auth_tokens {:id token
                                    :user_id user-id})))

(defn authenticate-token
  [req token]
  (let [sql-str (str "SELECT user_id FROM auth_tokens "
                      " WHERE id = ?")]
    (some-> (sql/query *db* [sql-str token])
      first
      :user_id
      users/select-user)))

(defn unauthorized-handler [req msg]
  {:status 401
   :body {:status :error
          :message (or msg "User not authorized")}})


(def auth-backend (token-backend {:authfn authenticate-token
                                  :unauthorized unauthorized-handler}))

(defn authenticate-user [req]
  (if (authenticated? req)
    true
    (error "User must be authenticated")))

(def rules [])