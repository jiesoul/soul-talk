(ns soul-talk.auth.routes
  (:require [compojure.api.sweet :refer :all]
            [soul-talk.auth.interface :as auth]
            [soul-talk.spec.core :refer [Result]]))

(def private-routes
  (context "" []
    :tags ["登录"]
    (POST "/login" req
      :return Result
      :body [user auth/login]
      :summary "用户登陆，登陆成功返回 Token"
      (auth/login! req user))

    (POST "/logout" req
      :return Result
      :body [auth-token auth/auth-token]
      :summary "用户登出"
      (auth/logout! auth-token))))