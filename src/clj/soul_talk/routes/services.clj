(ns soul-talk.routes.services
  (:require [soul-talk.routes.auth :as auth]
            [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [clojure.spec.alpha :as s]
            [soul-talk.routes.user :as user]
            [buddy.auth.accessrules :refer [restrict]]))

(defn admin?
  [request]
  (:identity request))

(defn access-error [__]
  (unauthorized {:result :error
                  :message "未经授权的"}))

(defn wrap-restricted [handler rule]
  (restrict handler {:handler rule
                      :on-error access-error}))

(s/def ::result keyword?)
(s/def ::message string?)
(s/def ::Result (s/keys :req-un [::result]
                        :opt-un [::message]))

(def email-regex #"^[^@]+@[^@\\.]+[\\.].+")
(s/def ::email-type (s/and string? #(re-matches email-regex %)))
(s/def ::password string?)
(s/def ::pass-confirm string?)
(s/def ::email ::email-type)
(s/def ::pass-old string?)
(s/def ::pass-new string?)

(s/def ::userReg (s/keys :req-un [::email ::password ::pass-confirm]))
(s/def ::userLogin (s/keys :req-un [::email ::password]))
(s/def ::userChangePass (s/keys :req-un [::email ::pass-old ::pass-new ::pass-confirm]))

(def services-routes
  (api
    {:coercion :spec
     :swagger
              {:ui   "/api-docs"
               :spec "/swagger.json"
               :data {:info     {:title       "Soul Talk API"
                                 :description "public API"}
                      :tags     [{:name "api" :description "apis"}]}}}

    (context "/api" []
      :tags ["api"]

      (POST "/register" req
        :return ::Result
        :body [user ::userReg]
        :summary "register a new user"
        (auth/register! req user))

      (POST "/login" req
        :return ::Result
        :body [user ::userLogin]
        :summary "User Login"
        (auth/login! req user))

      (POST "/logout" []
        :return ::Result
        :summary "user logout, and remove user session"
        (auth/logout!))


      (context "/admin" []
        :tags ["admin"]

        (GET "/users" req
          :return ::Result
          :summary "load-users"
          (user/load-users!))

        (POST "/change-pass" []
          :return ::Result
          :body [params ::userChangePass]
          :summary "User change password"
          (auth/change-pass! params))

        ))))