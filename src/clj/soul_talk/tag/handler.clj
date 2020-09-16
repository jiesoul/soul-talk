(ns soul-talk.tag.handler
  (:require [soul-talk.tag.db :as tag-db]
            [soul-talk.base.common :refer [handler]]
            [ring.util.http-response :as resp]
            [taoensso.timbre :as log]
            [clojure.spec.alpha :as s]))

(s/def ::name string?)
(def Tag (s/def ::Tag (s/keys :req-un [::name])))

(defn get-all-tags []
  (let [tags (tag-db/get-tags)]
    (resp/ok {:result :ok
              :tags tags})))

(defn insert-tag! [tag]
  (let [t (tag-db/save-tag! tag)]
    (resp/ok {:result :ok
              :tag t})))