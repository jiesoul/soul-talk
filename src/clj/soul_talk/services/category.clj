(ns soul-talk.services.category
  (:require [soul-talk.handlers.category :as category]
            [buddy.auth :refer [authenticated?]]
            [soul-talk.handlers.common :as common]
            [compojure.api.sweet :refer [context GET POST PUT DELETE]]))

(def category-routes

  (context "/categories" []

    (GET "/" []
      :return ::common/Result
      :summary "load categories"
      (category/get-all-categories))

    (GET "/:id" []
      :path-params [id :- ::category/id]
      :return ::common/Result
      :summary "Load category by id"
      (category/get-category-by-id id))

    ;;follow auth
    (POST "/" []
      :auth-rules authenticated?
      :return ::common/Result
      :body [category category/Category]
      :summary "create category"
      (category/save-category! category))

    (PUT "/" []
      :auth-rules authenticated?
      :return ::common/Result
      :body [category category/Category]
      :summary "update category"
      (category/update-category! category))

    (DELETE "/:id" [id]
      :auth-rules authenticated?
      :return ::common/Result
      :summary "delete category"
      (category/delete-category! (Integer/parseInt id))))

  )
