(ns soul-talk.auth.routes
  (:require [compojure.api.sweet :refer :all]
            [soul-talk.auth.handler :as handler]
            [soul-talk.auth.spec :as spec]
            [soul-talk.spec.core :refer [Result]]))

(def auth-routes
  (context "" []
    :tags ["登陆"]
    (POST "/login" req
      :return Result
      :body [user spec/login]
      :summary "用户登陆，登陆成功返回 Token"
      (handler/login! req user))

    (POST "/logout" req
      :return Result
      :body [auth-token spec/auth-token]
      :summary "用户登出"
      (handler/logout! auth-token))
    ))