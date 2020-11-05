(ns soul-talk.tag.routes
  (:require [compojure.api.sweet :refer :all]
            [soul-talk.tag.spec :as spec]
            [soul-talk.spec.core :refer [Result]]
            [soul-talk.tag.handler :as handler]))

(def tag-routes
  (context "/tags" []
    :tags ["标签"]
    (GET "/" []
      :return Result
      :summary "得到所有标签"
      (handler/get-all-tags))

    (POST "/" []
      :body [tag spec/tag]
      :return Result
      :summary "添加标签"
      (handler/insert-tag! tag))

    (PATCH "/:id" []
      :path-params [id :- int?]
      :return Result
      :summary "修改标签")

    (DELETE "/:id" []
      :path-params [id :- int?]
      :return Result
      :summary "删除标签"
      (handler/delete-tag! id))
    ))