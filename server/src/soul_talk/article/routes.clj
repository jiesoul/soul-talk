(ns soul-talk.article.routes
  (:require [compojure.api.sweet :refer :all]
            [soul-talk.article.interface :as article]
            [soul-talk.spec.core :refer [Result]]
            [soul-talk.tag.handler :as tag]))

(def public-routes
  (context "/articles" []
    :tags ["文章"]
    (GET "/public" req
      :return Result
      :summary "查看所有发布的文章"
      (article/get-publish-article req))

    (GET "/public/tags/:tag-id" []
      :path-params [tag-id :- int?]
      :return Result
      :summary "查询某个标签下的已发布文章")

    (GET "/public/q" req
      :return Result
      :summary "根据条件查询文章")

    (GET "/archives" []
      :return Result
      :summary "查看发布文章的存档"
      (article/get-article-archives))

    (GET "/archives/:year/:month" []
      :return Result
      :path-params [year :- int? month :- int?]
      :summary "按年月查看发布存档"
      (article/get-article-archives-year-month year month))

    (GET "/:id" [id]
      :return Result
      :summary "查看文章"
      (article/get-article id))

    (GET "/:id/tags" []
      :path-params [id :- string?]
      :return Result
      :summary "获取文章标签"
      (tag/get-tags-by-article-id id))

    (POST "/:id/comments" []
      :summary "发布评论"
      :return Result)

    (GET "/:id/comments" []
      :summary "查看文章评论"
      :return Result)

    ))

(def private-routes
  (context "/articles" []
    :tags ["文章"]
    ;; auth
    (GET "/" request
      :return Result
      :summary "查看所有文章"
      (article/get-all-articles request))

    (POST "/" []
      :return Result
      :body [article article/create-article]
      :summary "添加文章"
      (article/insert-article! article))

    (PUT "/:id" []
      :return Result
      :body [article article/update-article]
      :summary "更新文章"
      (article/update-article! article))

    (DELETE "/:id" [id]
      :return Result
      :summary "删除文章"
      (article/delete-article! id))

    (PATCH "/:id/publish" []
      :path-params [id :- string?]
      :return Result
      :summary "发布文章"
      (article/publish-article! id))))