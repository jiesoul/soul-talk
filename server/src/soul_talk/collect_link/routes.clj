(ns soul-talk.collect-link.routes
  (:require [compojure.api.sweet :refer :all]
            [soul-talk.collect-link.interface :as collect-link]
            [soul-talk.spec.core :refer [Result]]))

(def private-routes
  (context "/collect-links" []
    :tags ["collect-link"]

    (GET "/" req
      :summary "获取全部"
      :return Result
      (collect-link/load-collect-link req))

    (POST "/" []
      :summary "保存"
      :body [collect-link collect-link/create-collect-link]
      :return Result
      (collect-link/save-collect-link collect-link))

    (DELETE "/:id" []
      :return Result
      :path-params [id :- int?]
      :summary "删除key"
      (collect-link/delete-collect-link id))

    ))
