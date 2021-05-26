(ns soul-talk.role.routes
  (:require [soul-talk.role.handler :as role]
            [compojure.api.sweet :refer :all]
            [compojure.api.meta :refer [restructure-param]]
            [soul-talk.middleware :as m]))

(defmethod restructure-param :auth-app-key
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-app-key rule]))

(defmethod restructure-param :auth-login
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-auth rule]))

(def site-routes
  (context "/roles" []
    :tags ["角色"]

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

    (GET "/" req
      :summary "条件查询"
      :auth-login #{"admin"}
      (role/load-roles-page req))

    (GET "/menus" []
      :query-params [ids :- string?]
      :auth-login #{"admin"}
      (role/get-role-menus-by-ids ids))

    (context "/:id" []
      :path-params [id :- int?]

      (GET "/" []
        :summary "获取角色"
        :auth-login #{"admin"}
        :path-params [id :- int?]
        (role/get-role id))


      (DELETE "/" []
        :summary "删除角色"
        :auth-login #{"admin"}
        :path-params [id :- int?]
        (role/delete-role! id))


      (GET "/menus" []
        :summary "获取角色权限菜单"
        :auth-login #{"admin"}
        :path-params [id :- int?]
        (role/get-role-menus id)))

    ))