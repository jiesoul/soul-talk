(ns soul-talk.collect-site.routes
  (:require [compojure.api.sweet :refer :all]
            [soul-talk.collect-site.handler :as collect-site]
            [soul-talk.spec.core :refer [Result]]))

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

    (DELETE "/:id" []
      :auth-rules #{"admin"}
      :return Result
      :path-params [id :- int?]
      :summary "删除"
      (collect-site/delete-collect-site id))

    ))