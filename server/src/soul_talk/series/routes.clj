(ns soul-talk.series.routes
  (:require [compojure.api.sweet :refer :all]
            [compojure.api.meta :refer [restructure-param]]
            [soul-talk.spec.core :refer [Result]]
            [soul-talk.series.handler :as series]
            [soul-talk.middleware :as m]))

(defmethod restructure-param :auth-app-key
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-app-key rule]))

(defmethod restructure-param :auth-rules
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-auth rule]))

(def private-routes
  (context "series" []
    :tags ["系列"]

    (POST "/" []
      :auth-rules #{"admin"}
      :summary "保存系列"
      :body [series series/create-series]
      (series/save-series series))

    (PATCH "/" []
      :auth-rules #{"admin"}
      :summary "更新"
      :body [series series/update-series]
      (series/update-series series))

    (DELETE "/:id" []
      :auth-rules #{"admin"}
      :summary "删除"
      :path-params [id :- int?]
      (series/delete-series id))

    (GET "/" req
      :auth-rules #{"admin"}
      :summary "所有系列"
      :return Result
      (series/load-series-page req))
    ))