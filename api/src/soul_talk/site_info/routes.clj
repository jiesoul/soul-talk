(ns soul-talk.site-info.routes
  (:require [soul-talk.site-info.handler :as handler]
            [compojure.api.meta :refer [restructure-param]]
            [compojure.api.sweet :refer :all]
            [soul-talk.spec.core :refer [Result]]
            [soul-talk.middleware :as m]))

(defmethod restructure-param :auth-app-key
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-app-key rule]))

(defmethod restructure-param :auth-login
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-auth rule]))

(def api-routes
  (context "/site-info" []
    :tags ["网站信息"]
    (GET "/:id" []
      :summary "获取网站基本信息"
      :auth-app-key #{"admin"}
      :path-params [id :- int?]
      (handler/get-site-info id))
    ))

(def site-routes
  (context "/site-info" []
    (GET "/:id" []
      :summary "获取网站基本信息"
      :path-params [id :- int?]
      (handler/get-site-info id))

    (PATCH "/" []
      :summary "更新网站信息"
      :auth-login #{"admin"}
      :body [site-info handler/update-site-info]
      (handler/update-site-info! site-info))
    ))