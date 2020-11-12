(ns soul-talk.app-key.routes
  (:require [compojure.api.sweet :refer :all]
            [soul-talk.app-key.handler :as app-key]
            [soul-talk.spec.core :refer [Result]]))

(def private-routes
  (context "/app-keys" []
    :tags ["app-key"]

    (GET "/" req
      :summary "获取全部app key"
      :return Result
      (app-key/load-app-keys-page req))

    (POST "/" []
      :summary "保存"
      :body [app-key app-key/create-app-key]
      :return Result
      (app-key/save-app-key app-key))

    (GET "/gen" []
      :summary "生成KEY"
      :return Result
      (app-key/gen-app-key))

    (DELETE "/:id" []
      :return Result
      :path-params [id :- int?]
      :summary "删除key"
      (app-key/delete-app-key id))

    (GET "/q" req
      :return Result
      :summary "条件查询"
      (app-key/query-app-key req))

    ))
