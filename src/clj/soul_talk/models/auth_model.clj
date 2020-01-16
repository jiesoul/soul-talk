(ns soul-talk.models.auth-model
  (:require [crypto.random :refer [base64]]
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
    (sql/insert! *db* :auth_tokens {:id token
                                    :user_id user-id})))

(defn authenticate-token?
  [req token]
  (log/debug "auth request: " req)
  (let [sql-str (str "SELECT * FROM auth_tokens "
                      " WHERE id = ? and create_at + interval '10 h' > now()")
        tokens (sql/query *db* [sql-str token])]
    (some-> tokens
      first
      :user_id
      users/find-by-id)))

