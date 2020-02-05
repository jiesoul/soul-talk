(ns soul-talk.services.user
  (:require [soul-talk.handlers.user :as user]
            [soul-talk.handlers.auth :refer [authenticated]]
            [soul-talk.handlers.common :as common]
            [compojure.api.sweet :refer [context POST GET PATCH DELETE]]))

(def user-routes
  (context "/users" []
    :auth-rules authenticated

    (GET "/" []
      :return ::common/Result
      :summary "load-users"
      (user/load-users!))

    (GET "/:id" [id]
      :return ::user/User
      :summary "load user by id"
      (user/load-user id))

    (POST "/:id/change-password" []
      :return ::common/Result
      :body [params user/ChangePassUser]
      :summary "User change password"
      (user/change-pass! params))

    (PUT "/:id/profile" []
      :return ::common/Result
      :body [user user/User]
      :summary "User Profile update"
      (user/save-user-profile! user))))