(ns soul-talk.routes.tag
  (:require [soul-talk.models.tag-db :as tag-db]
            [soul-talk.routes.common :refer [handler]]
            [ring.util.http-response :as resp]
            [taoensso.timbre :as log]))

(handler get-all-tags []
  (let [tags (tag-db/get-tags)]
    (resp/ok {:result :ok
              :tags tags})))