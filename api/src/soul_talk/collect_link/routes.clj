(ns soul-talk.collect-link.routes
  (:require [compojure.api.sweet :refer :all]
            [compojure.api.meta :refer [restructure-param]]
            [soul-talk.collect-link.handler :as collect-link]
            [soul-talk.spec.core :refer [Result]]
            [soul-talk.middleware :as m]))

(defmethod restructure-param :auth-app-key
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-app-key rule]))

(defmethod restructure-param :auth-login
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-auth rule]))

(def api-routes
  (context "/collect-links" []
    :tags ["链接"]
    :auth-app-key #{"admin"}

    (GET "/" req
      :summary "获取全部"
      :return Result
      (collect-link/load-collect-links-page req))

    (GET "/:id" []
      :summary "get link"
      :path-params [id :- int?]
      :return Result
      (collect-link/get-collect-link id))

    (GET "/:id/tags" []
      :summary "get link tags"
      :path-params [id :- int?]
      :return Result
      (collect-link/get-collect-link-tags id))

    (GET "/:id/series" []
      :summary "get link series"
      :path-params [id :- int?]
      :return Result
      (collect-link/get-collect-link-series id))

    (POST "/:id/comments" []
      :summary "get link comments"
      :path-params [id :- int?]
      :body [collect-link-comment collect-link/collect-link-comment]
      :return Result
      (collect-link/save-collect-link-comment! collect-link-comment))

    (GET "/:id/comments" []
      :summary "get link comments"
      :path-params [id :- int?]
      :return Result
      (collect-link/get-comments-by-collect-link-id id))

    ))

(def site-routes
  (context "/collect-links" []
    :tags ["collect-link"]

    (GET "/" req
      :auth-login #{"admin"}
      :summary "获取全部"
      :return Result
      (collect-link/load-collect-links-page req))

    (POST "/" []
      :auth-login #{"admin"}
      :summary "保存"
      :body [collect-link collect-link/create-collect-link]
      :return Result
      (collect-link/save-collect-link collect-link))

    (PATCH "/" []
      :auth-login #{"admin"}
      :summary "更新"
      :body [collect-link collect-link/update-collect-link]
      :return Result
      (collect-link/update-collect-link collect-link))

    (GET "/:id" []
      :auth-login #{"admin"}
      :summary "查看"
      :path-params [id :- string?]
      (collect-link/get-collect-link id))

    (DELETE "/:id" []
      :auth-login #{"admin"}
      :return Result
      :path-params [id :- int?]
      :summary "删除key"
      (collect-link/delete-collect-link id))


    (context "/:id/tags" []

      (POST "/" []
        :auth-login #{"admin"}
        :summary "保存链接标签"
        :return Result
        :path-params [id :- string?]
        :body [collect-link-tag collect-link/collect-link-tag]
        (collect-link/save-collect-link-tag! collect-link-tag))

      (GET "/" []
        :auth-login #{"admin"}
        :summary "查看所有链接标签"
        :return Result
        :path-params [id :- string?]
        (collect-link/get-collect-link-tags id))


      (DELETE "/:tag-id" []
        :auth-login #{"admin"}
        :summary "删除链接标签"
        :return Result
        :path-params [id :- string?
                      tag-id :- int?]
        (collect-link/delete-collect-link-tag-by-id! tag-id))


      (DELETE "/" []
        :auth-login #{"admin"}
        :summary "删除链接所有标签"
        :return Result
        :path-params [id :- string?]
        (collect-link/delete-collect-link-tag-by-collect-link-id! id))
      )

    (context "/:id/series" []
      (POST "/" []
        :auth-login #{"admin"}
        :summary "保存系列"
        :return Result
        :path-params [id :- string?]
        :body [collect-link-series collect-link/collect-link-series]
        (collect-link/save-collect-link-series! collect-link-series))

      (GET "/" []
        :auth-login #{"admin"}
        :summary "查看所属系列"
        :return Result
        :path-params [id :- string?]
        (collect-link/get-collect-link-series id))


      (DELETE "/:series_id" []
        :auth-login #{"admin"}
        :summary "删除链接某个系列"
        :return Result
        :path-params [id :- string?
                      series_id :- int?]
        (collect-link/delete-collect-link-series-by-id! series_id))


      (DELETE "/" []
        :auth-login #{"admin"}
        :summary "删除链接所有系列"
        :return Result
        :path-params [id :- string?]
        (collect-link/delete-collect-link-series-by-collect-link-id! id))
      )

    (context "/:id/comments" []

      (GET "/" req
        :auth-login #{"admin"}
        :summary "评论列表"
        :return Result
        (collect-link/load-collect-links-comments-page req))

      (DELETE "/:comment-id" []
        :auth-login #{"admin"}
        :summary "删除某条评论"
        :path-params [id :- string?
                      comment-id :- int?]
        (collect-link/delete-collect-link-comment-by-id! comment-id))

      (DELETE "/" []
        :auth-login #{"admin"}
        :summary "删除链接所有评论"
        :path-params [id :- string?]
        (collect-link/delete-collect-link-comments-by-collect-link-id! id)))

    ))
