(ns soul-talk.auth.routes
  (:require [compojure.api.sweet :refer :all]
            [soul-talk.auth.handler :as auth]
            [soul-talk.spec.core :refer [Result]]))

(def login-routes
  (context "" []
    :tags ["登录"]
    (POST "/login" req
      :return Result
      :body [user auth/login]
      :summary "用户登陆，登陆成功返回 Token"
      (auth/login! req user))

    (POST "/logout" req
      :return Result
      :summary "用户登出"
      (auth/logout!))))