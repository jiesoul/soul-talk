(ns soul-talk.user.routes
  (:require [compojure.api.sweet :refer :all]
            [compojure.api.meta :refer [restructure-param]]
            [soul-talk.user.handler :as handler]
            [soul-talk.spec.core :refer [Result]]
            [soul-talk.middleware :as m]))

;; 多重方法用来注入中间件
(defmethod restructure-param :auth-app-key
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-app-key rule]))

(defmethod restructure-param :auth-login
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-auth rule]))

(def public-routes
  (context "/users" []
    :tags ["用户"]

    (GET "/:id/profile" []
      :auth-app-key #{"admin"}
      :return Result
      :path-params [id :- int?]
      :summary "查看个人信息"
      (handler/get-user-by-id id))))
    

(def private-routes
  (context "/users" []
    :tags ["用户"]

    (GET "/" req
      :auth-login #{"admin"}
      :return Result
      :summary "查看所有用户"
      (handler/load-users-page req))

    (GET "/auth-keys" req
      :auth-login #{"admin"}
      :return Result
      :summary "查看登录记录"
      (handler/load-users-auth-keys-page req))

    (context "/:id" []
      :path-params [id :- int?]

      (GET "/" []
        :summary "获取个人信息"
        :auth-login #{"admin"}
        :return Result
        (handler/get-user-by-id id))

      (PATCH "/" []
        :summary "修改用户信息"
        :auth-login #{"admin"}
        :body [user-profile handler/profile-user]
        :return Result
        (handler/update-user! id user-profile))

      (PATCH "/password" []
        :auth-login #{"admin"}
        :return Result
        :body [update-password handler/update-password]
        :summary "更改用户密码"
        (handler/update-password! id update-password))

      (DELETE "/" []
        :summary "删除用户"
        :auth-login #{"admin"}
        :return Result
        (handler/delete-user! id))

      (GET "/roles" []
        :summary "获取用户角色"
        :auth-login #{"admin"}
        :return Result
        (handler/get-user-roles id)))


    ))

      


