(ns soul-talk.collect-link.routes
  (:require [compojure.api.sweet :refer :all]
            [compojure.api.meta :refer [restructure-param]]
            [soul-talk.collect-link.handler :as collect-link]
            [soul-talk.spec.core :refer [Result]]
            [soul-talk.middleware :as m]))

(defmethod restructure-param :auth-app-key
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-app-key rule]))

(defmethod restructure-param :auth-rules
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-auth rule]))

(def private-routes
  (context "/collect-links" []
    :tags ["collect-link"]

    (GET "/" req
      :auth-rules #{"admin"}
      :summary "获取全部"
      :return Result
      (collect-link/load-collect-links-page req))

    (POST "/" []
      :auth-rules #{"admin"}
      :summary "保存"
      :body [collect-link collect-link/create-collect-link]
      :return Result
      (collect-link/save-collect-link collect-link))

    (PATCH "/" []
      :auth-rules #{"admin"}
      :summary "更新"
      :body [collect-link collect-link/update-collect-link]
      :return Result
      (collect-link/update-collect-link collect-link))

    (GET "/:id" []
      :auth-rules #{"admin"}
      :summary "查看"
      :path-params [id :- string?]
      (collect-link/get-collect-link id))

    (DELETE "/:id" []
      :auth-rules #{"admin"}
      :return Result
      :path-params [id :- int?]
      :summary "删除key"
      (collect-link/delete-collect-link id))

    ))
