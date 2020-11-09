(ns soul-talk.user.routes
  (:require [compojure.api.sweet :refer :all]
            [soul-talk.user.handler :as handler]
            [soul-talk.spec.core :refer [Result]]
            [soul-talk.user.spec :as spec]))

(def public-routes
  (context "/Users" []
    :tags ["用户"]
    (GET "/:id/profile" []
      :return Result
      :path-params [id :- int?]
      :summary "查看个人信息"
      (handler/get-user-profile id))
    ))

(def private-routes
  (context "/users" []
    :tags ["用户"]
    (GET "/" []
      :return Result
      :summary "查看所有用户"
      (handler/load-users))

    (PATCH "/:id/password" []
      :return Result
      :path-params [id :- int?]
      :body [update-password spec/update-password]
      :summary "更改用户密码"
      (handler/update-password! id update-password))

    (PATCH "/:id/profile" []
      :path-params [id :- int?]
      :body [user-profile spec/profile-user]
      :return Result
      :summary "修改用户信息"
      (handler/save-user-profile! id user-profile))))


