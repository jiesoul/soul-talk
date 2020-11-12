(ns soul-talk.serials.routes
  (:require [compojure.api.sweet :refer :all]
            [soul-talk.spec.core :refer [Result]]
            [soul-talk.serials.handler :as serials]))

(def private-routes
  (context "serials" []
    :tags ["系列"]

    (POST "/" []
      :summary "保存系列"
      :body [serials serials/create-serials]
      (serials/save-serials serials))

    (PATCH "/" []
      :summary "更新"
      :body [serials serials/update-serials]
      (serials/update-serials serials))

    (DELETE "/:id" []
      :summary "删除"
      :path-params [id :- int?]
      (serials/delete-serials id))

    (GET "/" req
      :summary "所有系列"
      :return Result
      (serials/load-serials-page req))
    ))