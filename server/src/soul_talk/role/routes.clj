(ns soul-talk.role.routes
  (:require [soul-talk.role.handler :as role]
            [compojure.api.sweet :refer :all]
            [compojure.api.meta :refer [restructure-params]]
            [soul-talk.middleware :as m]))

(defmethod restructure-param :auth-app-key
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-app-key rule]))

(defmethod restructure-param :auth-login
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-auth rule]))

(def site-routes
  (context "/roles" []

    (POST "/" []
      :summary "保存角色"
      :auth-login #{"admin"}
      :body [role role/create-role]
      (role/save-role! role))

    (PATCH "/" []
      :summary "更新角色"
      :auth-login #{"admin"}
      :body [role role/update-role]
      (role/update-role! role))

    (DELETE "/:id" []
      :summary "删除角色"
      :auth-login #{"admin"}
      :path-params [id :- int?]
      (role/delete-role! id))

    (GET "/:id" []
      :summary "获取角色"
      :auth-login #{"admin"}
      :path-params [id :- int?]
      (role/get-role id))

    (GET "/" req
      :summary "条件查询"
      :auth-login #{"admin"}
      (role/load-roles-page req))

    (GET "/:id/menus" []
      :summary "获取角色权限菜单"
      :auth-login #{"admin"}
      :path-params [id :- int?]
      (role/get-role-menus id))
    ))