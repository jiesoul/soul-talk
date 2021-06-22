(ns soul-talk.reply.routes
  (:require [compojure.api.sweet :refer :all]
            [compojure.api.meta :refer [restructure-param]]
            [soul-talk.reply.handler :as reply]
            [soul-talk.spec.core :refer [Result]]
            [soul-talk.middleware :as m]))

(defmethod restructure-param :auth-app-key
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-app-key rule]))

(defmethod restructure-param :auth-login
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-auth rule]))

(def private-routes
  (context "/replies" []
    :tags ["回复"]
    ;; auth

    (GET "/" request
      :auth-login #{"admin"}
      :return Result
      :summary "查看所有回复"
      (reply/load-replies-page request))

    (context "/:id" []

      (GET "/" [id]
        :auth-login #{"admin"}
        :return Result
        :summary "查看回复"
        (reply/get-reply id))

      (DELETE "/" [id]
        :auth-login #{"admin"}
        :return Result
        :summary "删除回复"
        (reply/delete-reply! id)))))