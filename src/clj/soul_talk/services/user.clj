(ns soul-talk.services.user
  (:require [soul-talk.handlers.user :as user]
            [soul-talk.handlers.auth :refer [authenticated]]
            [compojure.api.sweet :refer [context POST GET]]))

(def routes
  (context "user"

    (GET "/" []
      :return ::services/Result
      :summary "load-users"
      (user/load-users!))

    (POST "/change-password" []
      :auth-rules authenticated
      :return ::services/Result
      :body [params user/ChangePassUser]
      :summary "User change password"
      (user/change-pass! params))

    (POST "/user-profile" []
      :return ::services/Result
      :body [user user/User]
      :summary "User Profile update"
      (user/save-user-profile! user))))