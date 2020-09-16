(ns soul-talk.rest-api.handler
  (:require [compojure.api.sweet :refer :all]
            [buddy.auth :refer [authenticated?]]
            [soul-talk.article.interface :as article]
            [soul-talk.comment.interface :as comment]
            [soul-talk.spec.core :as spec]
            [soul-talk.tag.interface :as tag]
            [soul-talk.user.interface :as user]
            [clojure.spec.alpha :as s]))

(def auth-routes
  (routes
    (POST "/login" req
      :return ::spec/Result
      :body [user user/login]
      :summary "用户登陆，登陆成功返回 Token"
      (user/login! req user))

    (POST "/logout" []
      :return ::spec/Result
      :summary "用户登出"
      (user/logout!))

    (POST "/register" req
      :return ::spec/Result
      :body [user user/register]
      :summary "注册新用户"
      (user/register! req user))))

(def user-routes
  (context "/users" []
    ;:auth-rules authenticated?
    (GET "/" []
      :return ::spec/Result
      :summary "返回所有用户"
      (user/load-users))

    (PUT "/:id/password" []
      :return ::spec/Result
      :body [params user/update-password]
      :summary "更改用户密码"
      (user/update-password! params))

    (PUT "/:id/profile" []
      :return ::spec/Result
      :body [user user/profile-user]
      :summary "用户信息"
      (user/save-user-profile! user))))

(def article-routes
  (context "/articles" []

    (GET "/" req
      :return ::spec/Result
      :summary "返回所有公开的文章"
      (article/get-publish-article req))

    (GET "/archives" []
      :return ::spec/Result
      :summary "文章的存档"
      (article/get-article-archives))

    (GET "/archives/:year/:month" []
      :return ::spec/Result
      :path-params [year :- int? month :- int?]
      :summary "按年月存档"
      (article/get-article-archives-year-month year month))

    (GET "/:id" [id]
      :return ::spec/Result
      :summary "返回文章"
      (article/get-article id))

    ;;auth
    (routes
      ;:auth-rules authenticated?
      (GET "/" request
        :return ::spec/Result
        :summary "返回所有文章"
        (article/get-all-articles request))

      (POST "/" []
        :return ::spec/Result
        :body [post article/create-article]
        :summary "添加新的文章"
        (article/insert-article! post))

      (PUT "/:id" []
        :return ::spec/Result
        :body [post article/update-article]
        :summary "更新文章"
        (article/update-article! post))

      (DELETE "/:id" [id]
        :return ::spec/Result
        :summary "删除文章"
        (article/delete-article! id))

      (PUT "/:id/publish" [id]
        :return ::spec/Result
        :summary "发布文章"
        (article/publish-article! id))

      (POST "/upload" req
        :return ::spec/Result
        :summary "上传文章"
        (article/upload-article! req)))))

(def tag-routes
  (context "/tags" []
    (POST "/add" [tag]
      :return ::spec/Result
      ;:body [tag]
      :summary "create category"
      (tag/insert-tag! tag))))
