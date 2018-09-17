(ns soul-talk.routes.user
  (:require [soul-talk.models.user-db :as user-db]
            [ring.util.http-response :as resp]
            [taoensso.timbre :as log]))

(defn load-users! []
  (let [users (user-db/select-all-users)]
    (resp/ok {:result :ok
              :users users})))
