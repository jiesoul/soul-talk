(ns soul-talk.menu.routes
  (:require [soul-talk.menu.handler :as menu]
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
  (context "/menus" []
    :tags ["菜单"]

    (POST "/" []
      :summary "保存菜单"
      :auth-login #{"admin"}
      :body [menu menu/create-menu]
      (menu/save-menu! menu))

    (PATCH "/" []
      :summary "更新菜单"
      :auth-login #{"admin"}
      :body [menu menu/update-menu]
      (menu/update-menu! menu))

    (GET "/all" []
      :summary "所有菜单"
      :auth-login #{"admin"}
      (menu/load-menus-all))

    (GET "/" req
      :summary "条件查询"
      :auth-login #{"admin"}
      (menu/load-menus-page req))

    (context "/:id" []
      :path-params [id :- int?]

      (GET "/" []
        :summary "获取菜单"
        :auth-login #{"admin"}
        (menu/get-menu-by-id id))

      (DELETE "/" []
        :summary "删除菜单"
        :auth-login #{"admin"}
        (menu/delete-menu! id)))

    ))
