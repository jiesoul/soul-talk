(ns soul-talk.reply.handler
  (:require [soul-talk.reply.db :as reply-db]
            [soul-talk.utils :as utils]
            [java-time.local :as l]
            [java-time.format :as f]
            [soul-talk.pagination :as p]
            [soul-talk.reply.spec :as spec]
            [soul-talk.utils.id-util :as id-util]))

(def create-reply spec/create-reply)

(defn load-replies-page [req]
  (let [params (:params req)
        pagination (p/create req)
        [replies total] (reply-db/load-replies-page pagination params)
        pagination (p/create-total pagination total)]
    (utils/ok  {:pagination pagination
                :replies   replies
                :query-params params})))

(defn get-reply [reply-id]
  (let [reply (reply-db/get-reply-by-id reply-id)]
    (utils/ok "加载成功" {:reply reply})))

(defn insert-reply! [reply]
  (let [time (utils/now)
        id (id-util/gen-id!)
        reply (reply-db/insert-reply!
                (assoc reply :id id :create_at time :update_at time))]
    (utils/ok {:reply reply})))

(defn delete-reply! [id]
  (do
    (reply-db/delete-reply! id)
    (utils/ok "删除成功")))





