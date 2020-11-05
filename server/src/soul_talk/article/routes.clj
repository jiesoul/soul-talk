(ns soul-talk.article.routes
  (:require [compojure.api.sweet :refer :all]
            [soul-talk.article.handler :as handler]
            [soul-talk.article.spec :as spec]
            [soul-talk.spec.core :refer [Result]]
            [soul-talk.tag.handler :as tag]
            [soul-talk.comment.handler :as comment]))

(def article-routes
  (context "/articles" []
    :tags ["文章"]
    (GET "/public" req
      :return Result
      :summary "查看所有发布的文章"
      (handler/get-publish-article req))

    (GET "/public/tags/:tag-id" []
      :path-params [tag-id :- int?]
      :return Result
      :summary "查询某个标签下的已发布文章")

    (GET "/public/q=:query" []
      :path-params [query :- string?]
      :return Result
      :summary "根据条件查询文章")

    (GET "/archives" []
      :return Result
      :summary "查看发布文章的存档"
      (handler/get-article-archives))

    (GET "/archives/:year/:month" []
      :return Result
      :path-params [year :- int? month :- int?]
      :summary "按年月查看发布存档"
      (handler/get-article-archives-year-month year month))

    (GET "/:id" [id]
      :return Result
      :summary "查看文章"
      (handler/get-article id))

    (GET "/:id/tags" []
      :path-params [id :- string?]
      :return Result
      :summary "获取文章标签"
      (tag/get-tags-by-article-id id))

    (GET "/:id/comments" [id]
      :return Result
      :summary "文章评论"
      (comment/get-comments-by-articleId id))

    ;; auth
    (GET "/" request
      :return Result
      :summary "查看所有文章"
      (handler/get-all-articles request))

    (POST "/" []
      :return Result
      :body [article spec/create-article]
      :summary "添加文章"
      (handler/insert-article! article))

    (PUT "/:id" []
      :return Result
      :body [article spec/update-article]
      :summary "更新文章"
      (handler/update-article! article))

    (DELETE "/:id" [id]
      :return Result
      :summary "删除文章"
      (handler/delete-article! id))

    (PATCH "/:id/publish" []
      :path-params [id :- string?]
      :return Result
      :summary "发布文章"
      (handler/publish-article! id))

    ))