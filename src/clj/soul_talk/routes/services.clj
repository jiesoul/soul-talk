(ns soul-talk.routes.services
  (:require [soul-talk.routes.auth :as auth]
            [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [clojure.spec.alpha :as s]
            [soul-talk.routes.user :as user]
            [buddy.auth.accessrules :refer [restrict]]
            [buddy.auth :refer [authenticated?]]
            [compojure.api.meta :refer [restructure-param]]
            [soul-talk.middleware :refer [wrap-session-auth]]))

(defn admin?
  [request]
  (:identity request))

(defn authenticated [req]
  (authenticated? req))

(defn admin [req]
  (and (authenticated? req)
       (:identity req)))

;; 错误处理
(defn access-error [_val]
  (unauthorized val))

;; 包装处理规则
(defn wrap-restricted [handler rule]
  (restrict handler {:handler rule
                      :on-error access-error}))

;; 多重方法用来注入中间件
(defmethod restructure-param :auth-rules
  [_ rule acc]
  (update-in acc [:middleware] conj [wrap-restricted rule]))

(s/def ::result keyword?)
(s/def ::message string?)
(s/def ::Result (s/keys :req-un [::result]
                        :opt-un [::message]))

(def services-routes
  (api
    {:coercion :spec
     :swagger
              {:ui   "/api-docs"
               :spec "/swagger.json"
               :data {:info     {:title       "Soul Talk API"
                                 :description "public API"}
                      :tags     [{:name "api" :description "apis"}]}}
     :exceptionos
     {:handlers
      {;;
       }}}
    

    (context "/api" []
      :tags ["api"]

      (POST "/register" req
        :return ::Result
        :body [user user/RegUser]
        :summary "register a new user"
        (auth/register! req user))

      (POST "/login" req
        :return ::Result
        :body [user user/LoginUser]
        :summary "User Login"
        (auth/login! req user))

      (POST "/logout" []
        :return ::Result
        :summary "user logout, and remove user session"
        (auth/logout!))


      (context "/admin" []
        :middleware [wrap-session-auth]
        :auth-rules authenticated?
        :tags ["admin"]

        (GET "/users" []
          :return ::Result
          :summary "load-users"
          (user/load-users!))

        (POST "/change-pass" []
          :return ::Result
          :body [params user/ChangePassUser]
          :summary "User change password"
          (user/change-pass! params))

        (POST "/user-profile" []
          :return ::Result
          :body [user user/User]
          :summary "User Profile update"
          (user/save-user-profile! user)))
      )))
