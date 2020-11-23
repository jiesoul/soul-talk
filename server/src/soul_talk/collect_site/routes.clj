(ns soul-talk.collect-site.routes
  (:require [compojure.api.sweet :refer :all]
            [compojure.api.meta :refer [restructure-param]]
            [soul-talk.collect-site.handler :as collect-site]
            [soul-talk.spec.core :refer [Result]]
            [soul-talk.middleware :as m]))


(defmethod restructure-param :auth-app-key
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-app-key rule]))

(defmethod restructure-param :auth-rules
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-auth rule]))

(def public-routes
  (context "/collect-sites" []
    :tags ["网站"]
    :auth-app-key #{"admin"}

    (GET "/" req
      :summary "获取全部"
      :return Result
      (collect-site/load-collect-sites-page req))

    (GET "/:id" []
      :summary "get site"
      :path-params [id :- int?]
      :return Result
      (collect-site/get-collect-site id))


    (POST "/:id/comment" []
      :summary "add a comment"
      :body [collect-site-comment collect-site/collect-site-comment]
      :return Result)

    ))


(def private-routes
  (context "/collect-sites" []
    :tags ["收藏的网站"]

    (GET "/" req
      :auth-rules #{"admin"}
      :summary "获取全部"
      :return Result
      (collect-site/load-collect-sites-page req))

    (POST "/" []
      :auth-rules #{"admin"}
      :summary "保存"
      :body [collect-site collect-site/create-collect-site]
      :return Result
      (collect-site/save-collect-site collect-site))

    (PATCH "/" []
      :auth-rules #{"admin"}
      :summary "更新"
      :body [collect-site collect-site/update-collect-site]
      :return Result
      (collect-site/update-collect-site collect-site))

    (GET "/:id" []
      :auth-rules #{"admin"}
      :summary "get site"
      :path-params [id :- int?]
      :return Result
      (collect-site/get-collect-site id))

    (DELETE "/:id" []
      :auth-rules #{"admin"}
      :return Result
      :path-params [id :- int?]
      :summary "删除"
      (collect-site/delete-collect-site id))


    (context "/:id/tags" []

      (POST "/" []
        :auth-rules #{"admin"}
        :summary "保存网站标签"
        :return Result
        :path-params [id :- string?]
        :body [collect-site-tag collect-site/collect-site-tag]
        (collect-site/save-collect-site-tag! collect-site-tag))

      (GET "/" []
        :auth-rules #{"admin"}
        :summary "查看所有网站标签"
        :return Result
        :path-params [id :- string?]
        (collect-site/get-collect-site-tags id))


      (DELETE "/:tag-id" []
        :auth-rules #{"admin"}
        :summary "删除网站标签"
        :return Result
        :path-params [id :- string?
                      tag-id :- int?]
        (collect-site/delete-collect-site-tag-by-id! tag-id))


      (DELETE "/" []
        :auth-rules #{"admin"}
        :summary "删除网站所有标签"
        :return Result
        :path-params [id :- string?]
        (collect-site/delete-collect-site-tag-by-collect-site-id! id))
      )

    (context "/:id/series" []
      (POST "/" []
        :auth-rules #{"admin"}
        :summary "保存系列"
        :return Result
        :path-params [id :- string?]
        :body [collect-site-series collect-site/collect-site-series]
        (collect-site/save-collect-site-series! collect-site-series))

      (GET "/" []
        :auth-rules #{"admin"}
        :summary "查看所属系列"
        :return Result
        :path-params [id :- string?]
        (collect-site/get-collect-site-series id))


      (DELETE "/:series_id" []
        :auth-rules #{"admin"}
        :summary "删除网站某个系列"
        :return Result
        :path-params [id :- string?
                      series_id :- int?]
        (collect-site/delete-collect-site-series-by-id! series_id))


      (DELETE "/" []
        :auth-rules #{"admin"}
        :summary "删除网站所有系列"
        :return Result
        :path-params [id :- string?]
        (collect-site/delete-collect-site-series-by-collect-site-id! id))
      )

    (context "/:id/comments" []

      (GET "/" req
        :auth-rules #{"admin"}
        :summary "评论列表"
        :return Result
        (collect-site/load-collect-sites-comments-page req))

      (DELETE "/:comment-id" []
        :auth-rules #{"admin"}
        :summary "删除某条评论"
        :path-params [id :- string?
                      comment-id :- int?]
        (collect-site/delete-collect-site-comment-by-id! comment-id))

      (DELETE "/" []
        :auth-rules #{"admin"}
        :summary "删除网站所有评论"
        :path-params [id :- string?]
        (collect-site/delete-collect-site-comments-by-collect-site-id! id)))


    ))
