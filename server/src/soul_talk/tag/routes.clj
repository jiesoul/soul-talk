(ns soul-talk.tag.routes
  (:require [compojure.api.sweet :refer :all]
            [compojure.api.meta :refer [restructure-param]]
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
  (context "/tags" []
    :tags ["标签"]
    (GET "/hot" req
      :auth-app-key #{"admin"}
      :summary "查看多个标签"
      :return Result
      (tag/load-tags-page req))
    )
  )

(def private-routes
  (context "/tags" []
    :tags [""]

    (GET "/" req
      :auth-rules #{"admin"}
      :return Result
      :summary "得到所有标签"
      (tag/load-tags-page req))

    (POST "/" []
      :auth-rules #{"admin"}
      :body [tag tag/tag]
      :return Result
      :summary "添加标签"
      (tag/insert-tag! tag))

    (PATCH "/:id" []
      :auth-rules #{"admin"}
      :path-params [id :- int?]
      :return Result
      :summary "修改标签")

    (DELETE "/:id" []
      :auth-rules #{"admin"}
      :path-params [id :- int?]
      :return Result
      :summary "删除标签"
      (tag/delete-tag! id))))