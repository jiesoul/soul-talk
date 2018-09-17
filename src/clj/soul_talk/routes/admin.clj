(ns soul-talk.routes.admin
  (:require [ring.util.http-response :as resp]))

(defn dashboard! []
  (resp/ok {:result :ok
            :message "This is Dashboard"}))
