(ns soul-talk.category.routes
  (:require [compojure.api.sweet :refer :all]
            [compojure.api.meta :refer [restructure-param]]
            [soul-talk.spec.core :refer [Result]]
            [soul-talk.category.handler :as category]
            [soul-talk.middleware :as m]))

(defmethod restructure-param :auth-app-key
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-app-key rule]))

(defmethod restructure-param :auth-login
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-auth rule]))


(def api-routes
  (context "/category" []
    :tags ["系列"]
    :header-params [api-key :- string?]

    (GET "/" req
      :auth-app-key #{"admin"}
      :summary "query category by attribute"
      :return Result
      (category/load-category-page req))

    (GET "/:id" []
      :auth-app-key #{"admin"}
      :summary "get a category by id"
      :path-params [id :- int?]
      :return Result
      (category/get-category-by-id id))
    ))

(def site-routes
  (context "/category" []
    :tags ["系列"]

    (POST "/" []
      :auth-login #{"admin"}
      :summary "保存系列"
      :return Result
      :body [category category/create-category]
      (category/save-category category))

    (PATCH "/" []
      :auth-login #{"admin"}
      :summary "更新"
      :body [category category/update-category]
      (category/update-category category))

    (GET "/:id" []
      :auth-login #{"admin"}
      :summary "get a category by id"
      :path-params [id :- int?]
      :return Result
      (category/get-category-by-id id))

    (GET "/" req
      :auth-login #{"admin"}
      :summary "所有系列"
      :return Result
      (category/load-category-page req))

    (DELETE "/:id" []
      :auth-login #{"admin"}
      :summary "删除"
      :path-params [id :- int?]
      (category/delete-category id))

    ))