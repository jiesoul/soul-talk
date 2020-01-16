(ns soul-talk.services.auth
  (:require [soul-talk.handlers.auth :refer [register! login! logout!]]
            [compojure.api.sweet :refer [context POST]]
            [clojure.spec.alpha :as s]
            [soul-talk.handlers.user :as user]))

(def routes
  (POST "/register" req
    :return ::Result
    :body [user user/RegUser]
    :summary "register a new user"
    (register! req user))

  (POST "/login" req
    :return ::Result
    :body [user user/LoginUser]
    :summary "User Login"
    (login! req user))

  (POST "/logout" []
    :return ::Result
    :summary "user logout, and remove user session"
    (logout!)))
