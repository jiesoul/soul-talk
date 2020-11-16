(ns soul-talk.serials.routes
  (:require [compojure.api.sweet :refer :all]
            [compojure.api.meta :refer [restructure-param]]
            [soul-talk.spec.core :refer [Result]]
            [soul-talk.serials.handler :as serials]
            [soul-talk.middleware :as m]))

(defmethod restructure-param :auth-app-key
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-app-key rule]))

(defmethod restructure-param :auth-rules
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-auth rule]))

(def private-routes
  (context "serials" []
    :tags ["系列"]

    (POST "/" []
      :auth-rules #{"admin"}
      :summary "保存系列"
      :body [serials serials/create-serials]
      (serials/save-serials serials))

    (PATCH "/" []
      :auth-rules #{"admin"}
      :summary "更新"
      :body [serials serials/update-serials]
      (serials/update-serials serials))

    (DELETE "/:id" []
      :auth-rules #{"admin"}
      :summary "删除"
      :path-params [id :- int?]
      (serials/delete-serials id))

    (GET "/" req
      :auth-rules #{"admin"}
      :summary "所有系列"
      :return Result
      (serials/load-serials-page req))
    ))