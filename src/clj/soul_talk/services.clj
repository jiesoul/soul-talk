(ns soul-talk.services
  (:require [soul-talk.routes.auth :as auth]
            [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [clojure.spec.alpha :as s]
            [soul-talk.routes.user :as user]
            [compojure.api.meta :refer [restructure-param]]
            [soul-talk.routes.category :as category]
            [soul-talk.routes.tag :as tag]
            [soul-talk.routes.posts :as posts]
            [soul-talk.routes.files :as files]
            [expound.alpha :as expound]
            [soul-talk.middleware :refer [wrap-rule]]
            [soul-talk.models.auth-model :refer [authenticated]]
            [taoensso.timbre :as log]
            [ring.util.http-response :as response]
            [compojure.api.exception :as ex]
            [soul-talk.routes.user :refer [RegUser LoginUser]]))

(def printer
  (expound/custom-printer
    {:theme :figwheel-theme :print-spec? false}))

;; 错误处理
(defn exception-handler [f type]
  (fn [^Exception e data request]
    (f {:message (.getMessage e), :type type})))

;; 多重方法用来注入中间件
(defmethod restructure-param :auth-rules
  [_ rule acc]
  (update-in acc [:middleware] conj [wrap-rule rule]))

(s/def ::id int?)
(s/def ::result keyword?)
(s/def ::message string?)
(s/def ::Result (s/keys :req-un [::result]
                        :opt-un [::message]))

(s/def ::page int?)
(s/def ::pre-page int?)
(s/def ::Pagination (s/keys :opt-un [::page ::pre-page]))

(def exceptions-config
  {:handlers {::calm                  (exception-handler response/enhance-your-calm :calm)
              java.sql.SQLException   (exception-handler response/internal-server-error :sql)
              ::ex/request-validation (ex/with-logging ex/request-parsing-handler :info)
              ::ex/default (exception-handler response/internal-server-error :unknown)}})

(def swagger-config
  {:ui   "/api-docs"
   :spec "/swagger.json"
   :data {:info {:title       "Soul Talk API"
                 :description "public API"}
          :tags [{:name "api" :description "apis"}]}})

(def api-config
  {:exceptions exceptions-config
   :coercion   :spec
   :swagger swagger-config })

(def services-routes
  (api
    api-config
    (context "/api" []
      :tags ["api"]

      (POST "/register" req
        :return ::Result
        :body [user RegUser]
        :summary "register a new user"
        (auth/register! req user))

      (POST "/login" req
        :return ::Result
        :body [user LoginUser]
        :summary "User Login"
        (auth/login! req user))

      (POST "/logout" []
        :return ::Result
        :summary "user logout, and remove user session"
        (auth/logout!))

      (GET "/categories" []
        :return ::Result
        :summary "load categories"
        (category/get-all-categories))

      (GET "/categories/:id" []
        :path-params [id :- ::category/id]
        :return ::Result
        :summary "Load category by id"
        (category/get-category-by-id id))

      (GET "/tags" []
        :return ::Result
        :summary "load tags"
        (tag/get-all-tags))

      (context "/posts" []

        (GET "/" req
          :return ::Result
          :summary "load all publish posts"
          (posts/get-publish-posts req))

        (GET "/archives" []
          :return ::Result
          :summary "load post archives"
          (posts/get-posts-archives))

        (GET "/archives/:year/:month" []
          :return ::Result
          :path-params [year :- int? month :- int?]
          :summary "load post archives"
          (posts/get-posts-archives-year-month year month))

        (GET "/:id" [id]
          :return ::Result
          :summary "load post"
          (posts/get-post id)))

      (context "/admin" []
        :auth-rules authenticated
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
          (user/save-user-profile! user))

        (context "/categories" []

          (POST "/" []
            :return ::Result
            :body [category category/Category]
            :summary "create category"
            (category/save-category! category))

          (PUT "/" []
            :return ::Result
            :body [category category/Category]
            :summary "update category"
            (category/update-category! category))

          (DELETE "/:id" [id]
            :return ::Result
            :summary "delete category"
            (category/delete-category! (Integer/parseInt id))))

        (context "/tags" []

          (POST "/add" []
            :return ::Result
            :body [tag tag/Tag]
            :summary "create category"
            (tag/save-tag! tag)))

        (context "/posts" []

          (GET "/" request
            :return ::Result
            :summary "return all posts contains id not publish"
            (posts/get-all-posts request))

          (POST "/" []
            :return ::Result
            :body [post posts/Post]
            :summary "add a new post"
            (posts/save-post! post))

          (PUT "/:id" []
            :return ::Result
            :body [post posts/Post]
            :summary "update a post"
            (posts/update-post! post))

          (DELETE "/:id" [id]
            :return ::Result
            :summary "delete a post"
            (posts/delete-post! id))

          (PUT "/:id/publish" [id]
            :return ::Result
            :summary "publish a post"
            (posts/publish-post! id))

          (POST "/upload" req
            :return ::Result
            :summary "upload md file as post"
            (posts/upload-post! req)))

        (context "/files" []

          (POST "/md" req
            :return ::Result
            :summary "upload md file to str"
            (files/upload-md! req)))))))
