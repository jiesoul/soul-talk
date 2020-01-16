(ns soul-talk.services.category
  (:require [soul-talk.handlers.category :as category]
            [soul-talk.handlers.auth :refer [authenticated]]
            [compojure.api.sweet :refer [context GET POST PUT DELETE]]))


(def routes

  (context "/categories" []

    (GET "/categories" []
      :return ::Result
      :summary "load categories"
      (category/get-all-categories))

    (GET "/categories/:id" []
      :path-params [id :- ::category/id]
      :return ::Result
      :summary "Load category by id"
      (category/get-category-by-id id))

    ;;follow auth
    (POST "/" []
      :auth-rules authenticated
      :return ::Result
      :body [category category/Category]
      :summary "create category"
      (category/save-category! category))

    (PUT "/" []
      :auth-rules authenticated
      :return ::Result
      :body [category category/Category]
      :summary "update category"
      (category/update-category! category))

    (DELETE "/:id" [id]
      :auth-rules authenticated
      :return ::Result
      :summary "delete category"
      (category/delete-category! (Integer/parseInt id))))

  )
