(ns soul-talk.collect-site.routes
  (:require [compojure.api.sweet :refer :all]
            [soul-talk.collect-site.interface :as collect-site]
            [soul-talk.spec.core :refer [Result]]))

(def private-routes
  (context "/collect-sites" []
    :tags ["collect-site"]

    (GET "/" req
      :summary "获取全部app key"
      :return Result
      (collect-site/load-collect-site req))

    (POST "/" []
      :summary "保存apikey"
      :body [collect-site collect-site/create-collect-site]
      :return Result
      (collect-site/save-collect-site collect-site))

    (GET "/gen" []
      :summary "生成KEY"
      :return Result
      (collect-site/gen-collect-site))

    (DELETE "/:id" []
      :return Result
      :path-params [id :- int?]
      :summary "删除key"
      (collect-site/delete-collect-site id))

    (GET "/q" req
      :return Result
      :summary "条件查询"
      (collect-site/query-collect-site req))

    ))
