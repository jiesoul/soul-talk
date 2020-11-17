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
      :auth-rules #{"admin"}
      :summary "查看所有文章标签"
      :return Result
      :path-params [id :- string?]
      (article/get-article-tags id))

    (GET "/:id/series" []
      :auth-rules #{"admin"}
      :summary "查看所属系列"
      :return Result
      :path-params [id :- string?]
      (article/get-article-series id))

    (POST "/:id/comments" []
      :auth-app-key #{"admin"}
      :summary "发表评论"
      :path-params [id :- string?]
      :body [comment article/article-comment]
      (article/save-article-comment! comment))

    (GET "/" req
      :auth-rules #{"admin"}
      :summary "评论列表"
      :return Result
      (article/load-articles-comments-page req))

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

    (GET "/:id" [id]
      :auth-rules #{"admin"}
      :return Result
      :summary "查看文章"
      (article/get-article id))

    (PATCH "/" []
      :auth-rules #{"admin"}
      :return Result
      :body [article article/update-article]
      :summary "更新文章"
      (article/update-article! article))

    (PATCH "/:id/publish" []
      :auth-rules #{"admin"}
      :path-params [id :- string?]
      :return Result
      :summary "发布文章"
      (article/publish-article! id))

    (DELETE "/:id" [id]
      :auth-rules #{"admin"}
      :return Result
      :summary "删除文章"
      (article/delete-article! id))


    (context "/:id/tags" []

      (POST "/" []
        :auth-rules #{"admin"}
        :summary "保存文章标签"
        :return Result
        :path-params [id :- string?]
        :body [article-tag article/article-tag]
        (article/save-article-tag! article-tag))

      (GET "/" []
        :auth-rules #{"admin"}
        :summary "查看所有文章标签"
        :return Result
        :path-params [id :- string?]
        (article/get-article-tags id))


      (DELETE "/:tag-id" []
        :auth-rules #{"admin"}
        :summary "删除文章标签"
        :return Result
        :path-params [id :- string?
                      tag-id :- int?]
        (article/delete-article-tag-by-id! tag-id))


      (DELETE "/" []
        :auth-rules #{"admin"}
        :summary "删除文章所有标签"
        :return Result
        :path-params [id :- string?]
        (article/delete-article-tag-by-article-id! id))
      )

    (context "/:id/series" []
      (POST "/" []
        :auth-rules #{"admin"}
        :summary "保存系列"
        :return Result
        :path-params [id :- string?]
        :body [article-series article/article-series]
        (article/save-article-series! article-series))

      (GET "/" []
        :auth-rules #{"admin"}
        :summary "查看所属系列"
        :return Result
        :path-params [id :- string?]
        (article/get-article-series id))


      (DELETE "/:series_id" []
        :auth-rules #{"admin"}
        :summary "删除文章某个系列"
        :return Result
        :path-params [id :- string?
                      series_id :- int?]
        (article/delete-article-series-by-id! series_id))


      (DELETE "/" []
        :auth-rules #{"admin"}
        :summary "删除文章所有系列"
        :return Result
        :path-params [id :- string?]
        (article/delete-article-series-by-article-id! id))
      )

    (context "/:id/comments" []

      (GET "/" req
        :auth-rules #{"admin"}
        :summary "评论列表"
        :return Result
        (article/load-articles-comments-page req))

      (DELETE "/:comment-id" []
        :auth-rules #{"admin"}
        :summary "删除某条评论"
        :path-params [id :- string?
                      comment-id :- int?]
        (article/delete-article-comment-by-id! comment-id))

      (DELETE "/" []
        :auth-rules #{"admin"}
        :summary "删除文章所有评论"
        :path-params [id :- string?]
        (article/delete-article-comments-by-article-id! id)))
    ))