(ns soul-talk.api-key.routes
  (:require [compojure.api.sweet :refer :all]
            [soul-talk.api-key.interface :as api-key]
            [soul-talk.spec.core :refer [Result]]))

(def private-routes
  (context "/api-keys" []
    :tags ["api-key"]
    (POST "/" []
      :summary "保存apikey"
      :body [api-key api-key/create-api-key]
      :return Result
      (api-key/save-api-key api-key))

    (GET "/gen" []
      :summary "生成KEY"
      :return Result
      (api-key/gen-api-key))

    (DELETE "/:id" []
      :path-params [id :- int?]
      :summary "删除key"
      (api-key/delete-api-key id))
    ))
