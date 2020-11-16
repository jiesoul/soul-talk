(ns soul-talk.article.routes
  (:require [compojure.api.sweet :refer :all]
            [compojure.api.meta :refer [restructure-param]]
            [soul-talk.article.handler :as article]
            [soul-talk.spec.core :refer [Result]]
            [soul-talk.tag.handler :as tag]
            [soul-talk.middleware :as m]))

(defmethod restructure-param :auth-app-key
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-app-key rule]))

(defmethod restructure-param :auth-rules
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-auth rule]))

(def public-routes
  (context "/articles" []
    :tags ["文章"]

    (GET "/public" req
      :auth-app-key #{"admin"}
      :return Result
      :summary "查看所有发布的文章"
      (article/load-articles-publish-page req))

    (GET "/public/tags/:tag-id" []
      :auth-app-key #{"admin"}
      :path-params [tag-id :- int?]
      :return Result
      :summary "查询某个标签下的已发布文章")

    (GET "/public/q" req
      :auth-app-key #{"admin"}
      :return Result
      :summary "根据条件查询文章")

    (GET "/archives" []
      :auth-app-key #{"admin"}
      :return Result
      :summary "查看发布文章的存档"
      (article/get-article-archives))

    (GET "/archives/:year/:month" []
      :auth-app-key #{"admin"}
      :return Result
      :path-params [year :- int? month :- int?]
      :summary "按年月查看发布存档"
      (article/get-article-archives-year-month year month))

    (GET "/:id/publish" [id]
      :auth-app-key #{"admin"}
      :return Result
      :summary "查看文章"
      (article/get-article-public id))

    (GET "/:id/tags" []
      :auth-app-key #{"admin"}
      :path-params [id :- string?]
      :return Result
      :summary "获取文章标签"
      (tag/get-tags-by-article-id id))

    (POST "/:id/comments" []
      :auth-app-key #{"admin"}
      :summary "发表评论"
      :path-params [id :- string?]
      :body [comment article/create-comment]
      (article/save-article-comment id comment))

    (GET "/:id/comments" []
      :auth-app-key #{"admin"}
      :summary "查看文章评论"
      :return Result)

    ))

(def private-routes
  (context "/articles" []
    :tags ["文章"]
    ;; auth
    (GET "/" request
      :auth-rules #{"admin"}
      :return Result
      :summary "查看所有文章"
      (article/load-articles-page request))

    (POST "/" []
      :auth-rules #{"admin"}
      :return Result
      :body [article article/create-article]
      :summary "添加文章"
      (article/insert-article! article))

    (PATCH "/" []
      :auth-rules #{"admin"}
      :return Result
      :body [article article/update-article]
      :summary "更新文章"
      (article/update-article! article))

    (DELETE "/:id" [id]
      :auth-rules #{"admin"}
      :return Result
      :summary "删除文章"
      (article/delete-article! id))

    (GET "/:id" [id]
      :auth-rules #{"admin"}
      :return Result
      :summary "查看文章"
      (article/get-article id))

    (PATCH "/:id/publish" []
      :auth-rules #{"admin"}
      :path-params [id :- string?]
      :return Result
      :summary "发布文章"
      (article/publish-article! id))

    (GET "/comments" req
      :auth-rules #{"admin"}
      :summary "评论列表"
      :return Result
      (article/load-articles-comments-page req))

    (DELETE "/comments/:id" []
      :auth-rules #{"admin"}
      :summary "删除评论"
      :path-params [id :- int?]
      (article/delete-article-comment id))
    ))