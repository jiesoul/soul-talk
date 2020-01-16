(ns soul-talk.handlers.tag
  (:require [soul-talk.models.tag-db :as tag-db]
            [soul-talk.handlers.common :refer [handler]]
            [ring.util.http-response :as resp]
            [taoensso.timbre :as log]
            [clojure.spec.alpha :as s]))

(s/def ::name string?)
(def Tag (s/def ::Tag (s/keys :req-un [::name])))

(handler get-all-tags []
  (let [tags (tag-db/get-tags)]
    (resp/ok {:result :ok
              :tags tags})))


(handler save-tag! [tag]
  (let [t (tag-db/save-tag! tag)]
    (resp/ok {:result :ok
              :tag t})))