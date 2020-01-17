(ns soul-talk.services.auth
  (:require [soul-talk.handlers.auth :refer [register! login! logout!]]
            [compojure.api.sweet :refer [routes POST]]
            [soul-talk.handlers.auth :refer [authenticated]]
            [soul-talk.handlers.common :as common]
            [soul-talk.handlers.user :as user]))
(def auth-routes
  (routes
    (POST "/login" req
      :return ::common/Result
      :body [user user/LoginUser]
      :summary "User Login, if login ok then get token."
      (login! req user))

    (POST "/logout" []
      :return ::common/Result
      :summary "user logout, and remove user session"
      (logout!))

    (POST "/register" req
      :return ::common/Result
      :body [user user/RegUser]
      :summary "register a new user"
      (register! req user))))
