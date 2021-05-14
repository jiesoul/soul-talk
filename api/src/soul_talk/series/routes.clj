(ns soul-talk.series.routes
  (:require [compojure.api.sweet :refer :all]
            [compojure.api.meta :refer [restructure-param]]
            [soul-talk.spec.core :refer [Result]]
            [soul-talk.series.handler :as series]
            [soul-talk.middleware :as m]))

(defmethod restructure-param :auth-app-key
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-app-key rule]))

(defmethod restructure-param :auth-login
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-auth rule]))


(def api-routes
  (context "/series" []
    :tags ["系列"]
    :header-params ["api-key" :- string?]

    (GET "/" req
      :auth-app-key #{"admin"}
      :summary "query series by attribute"
      :return Result
      (series/load-series-page req))

    (GET "/:id" []
      :auth-app-key #{"admin"}
      :summary "get a series by id"
      :path-params [id :- int?]
      :return Result
      (series/get-series-by-id id))

    ))

(def site-routes
  (context "/series" []
    :tags ["系列"]

    (POST "/" []
      :auth-login #{"admin"}
      :summary "保存系列"
      :return Result
      :body [series series/create-series]
      (series/save-series series))

    (PATCH "/" []
      :auth-login #{"admin"}
      :summary "更新"
      :body [series series/update-series]
      (series/update-series series))

    (GET "/:id" []
      :auth-login #{"admin"}
      :summary "get a series by id"
      :path-params [id :- int?]
      :return Result
      (series/get-series-by-id id))

    (GET "/" req
      :auth-login #{"admin"}
      :summary "所有系列"
      :return Result
      (series/load-series-page req))

    (DELETE "/:id" []
      :auth-login #{"admin"}
      :summary "删除"
      :path-params [id :- int?]
      (series/delete-series id))

    ))