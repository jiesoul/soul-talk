(ns soul-talk.tag.routes
  (:require [compojure.api.sweet :refer :all]
            [soul-talk.spec.core :refer [Result]]
            [soul-talk.tag.interface :as tag]))

(def public-routes
  (context "/tags" []
    :tags ["标签"]
    (GET "/" []
      :return Result
      :query-params [name :- string?]
      :summary "得到所有标签"
      (tag/get-all-tags))

    (GET "/q" []
      :return Result
      :summary "根据名称模糊查询"
      :query-params [name :- string?]
      (tag/query-tags name))
    )
  )

(def private-routes
  (context "/tags" []
    :tags ["标签"]
    (POST "/" []
      :body [tag tag/tag]
      :return Result
      :summary "添加标签"
      (tag/insert-tag! tag))

    (PATCH "/:id" []
      :path-params [id :- int?]
      :return Result
      :summary "修改标签")

    (DELETE "/:id" []
      :path-params [id :- int?]
      :return Result
      :summary "删除标签"
      (tag/delete-tag! id))))