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
      :summary "查看所有文章"
      (reply/load-replies-page request))

    (POST "/" []
      :auth-login #{"admin"}
      :return Result
      :body [reply reply/create-reply]
      :summary "添加文章"
      (reply/insert-reply! reply))

    (context "/:id" []

      (GET "/" [id]
        :auth-login #{"admin"}
        :return Result
        :summary "查看文章"
        (reply/get-reply id))

      (PATCH "/" []
        :auth-login #{"admin"}
        :return Result
        :body [reply reply/update-reply]
        :summary "更新文章"
        (reply/update-reply! reply))

      (PATCH "/publish" []
        :auth-login #{"admin"}
        :path-params [id :- string?]
        :return Result
        :summary "发布文章"
        (reply/publish-reply! id))

      (DELETE "/" [id]
        :auth-login #{"admin"}
        :return Result
        :summary "删除文章"
        (reply/delete-reply! id))

      (context "/comments" []

        (GET "/" req
          :auth-login #{"admin"}
          :summary "评论列表"
          :return Result
          (reply/load-replies-comments-page req))

        (DELETE "/:comment-id" []
          :auth-login #{"admin"}
          :summary "删除某条评论"
          :path-params [id :- string?
                        comment-id :- int?]
          (reply/delete-reply-comment-by-id! comment-id))

        (DELETE "/" []
          :auth-login #{"admin"}
          :summary "删除文章所有评论"
          :path-params [id :- string?]
          (reply/delete-reply-comments-by-reply-id! id))))))