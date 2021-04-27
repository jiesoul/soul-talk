(ns soul-talk.app-key.routes
  (:require [compojure.api.sweet :refer :all]
            [soul-talk.app-key.handler :as app-key]
            [compojure.api.meta :refer [restructure-param]]
            [soul-talk.spec.core :refer [Result]]
            [soul-talk.middleware :as m]))

(defmethod restructure-param :auth-app-key
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-app-key rule]))

(defmethod restructure-param :auth-login
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-auth rule]))

(def site-routes
  (context "/app-keys" []
    :tags ["app-key"]

    (GET "/gen" []
      :auth-login #{"admin"}
      :summary "生成KEY"
      :return Result
      (app-key/gen-app-key))

    (GET "/" req
      :auth-login #{"admin"}
      :summary "获取全部app key"
      :return Result
      (app-key/load-app-keys-page req))

    (POST "/" []
      :auth-login #{"admin"}
      :summary "保存"
      :body [app-key app-key/create-app-key]
      :return Result
      (app-key/save-app-key! app-key))

    (GET "/:id" []
      :auth-login #{"admin"}
      :return Result
      :path-params [id :- int?]
      (app-key/get-app-key id))

    (PATCH "/" []
      :auth-login #{"admin"}
      :return Result
      :body [app-key app-key/update-app-key]
      (app-key/update-app-key! app-key))

    (DELETE "/:id" []
      :auth-login #{"admin"}
      :return Result
      :path-params [id :- int?]
      :summary "删除key"
      (app-key/delete-app-key! id))))