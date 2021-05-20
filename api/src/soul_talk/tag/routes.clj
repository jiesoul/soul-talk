(ns soul-talk.tag.routes
  (:require [compojure.api.sweet :refer :all]
            [compojure.api.meta :refer [restructure-param]]
            [soul-talk.spec.core :refer [Result]]
            [soul-talk.tag.handler :as tag]
            [soul-talk.middleware :as m]))

(defmethod restructure-param :auth-app-key
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-app-key rule]))

(defmethod restructure-param :auth-login
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-auth rule]))

(def public-routes
  (context "/tags" []
    :tags ["标签"]

    (GET "/hot" req
      :auth-app-key #{"admin"}
      :summary "hot tags"
      :return Result
      (tag/load-tags-page req))

    (GET "/" req
      :auth-app-key #{"admin"}
      :return Result
      :summary "得到所有标签"
      (tag/load-tags-page req))

    (GET "/:id" []
      :auth-app-key #{"admin"}
      :summary "查看标签"
      :path-params [id :- int?]
      :return Result
      (tag/get-tag-by-id id))
    ))

(def private-routes
  (context "/tags" []
    :tags ["标签"]
    :header-params [api-key :- string?]

    (POST "/" []
      :auth-login #{"admin"}
      :summary "添加标签"
      :return Result
      :body [tag tag/tag]
      (tag/save-tag! tag))

    (GET "/" req
      :auth-login #{"admin"}
      :return Result
      :summary "得到所有标签"
      (tag/load-tags-page req))

    (GET "/:id" []
      :auth-login #{"admin"}
      :summary "查看标签"
      :path-params [id :- int?]
      :return Result
      (tag/get-tag-by-id id))

    (DELETE "/:id" []
      :auth-login #{"admin"}
      :path-params [id :- int?]
      :return Result
      :summary "删除标签"
      (tag/delete-tag! id))))