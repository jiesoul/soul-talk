(ns soul-talk.rest-api.handler
  (:require [compojure.api.sweet :refer :all]
            [soul-talk.article.interface :as article]
            [soul-talk.comment.interface :as comment]
            [soul-talk.spec.core :as spec]
            [soul-talk.tag.interface :as tag]
            [soul-talk.user.interface :as user]
            [clojure.spec.alpha :as s]))

(def public-routes
  (routes
    :tags ["public api"]
    (context "" []
      :tags ["users"]
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
        (user/register! req user)))

    (context "/users" []
      :tags ["users"]
      (GET "/:id/profile" [id]
        :return ::spec/Result
        :path-params [id :- int?]
        :summary "查看个人信息"
        (user/get-user-profile id)))

    (context "/articles" []
      :tags ["articles"]
      (GET "/public" req
        :return ::spec/Result
        :summary "查看所有发布的文章"
        (article/get-publish-article req))
      (GET "/archives" []
        :return ::spec/Result
        :summary "查看发布文章的存档"
        (article/get-article-archives))

      (GET "/archives/:year/:month" []
        :return ::spec/Result
        :path-params [year :- int? month :- int?]
        :summary "按年月查看发布存档"
        (article/get-article-archives-year-month year month))

      (GET "/:id" [id]
        :return ::spec/Result
        :summary "查看文章"
        (article/get-article id)))

    (context "/tags" []
      :tags ["tags"]
      (POST "/add" [tag]
        :return ::spec/Result
        ;:body [tag]
        :summary "标签"
        (tag/insert-tag! tag)))

    (context "/comments" []
      :tags ["comments"]
      (GET "/:articleId/" [articleId]
        :return ::spec/Result))))

(def private-routes
  (routes
    :tags ["private api"]
    :auth-rules user/authenticated
    (context "/users" []
      :tags ["users"]
      ;; auth
      (GET "/" []
        :return ::spec/Result
        :summary "查看所有用户"
        (user/load-users))

      (PUT "/:id/password" []
        :return ::spec/Result
        :path-params [id :- int?]
        :body [update-password user/update-password]
        :summary "更改用户密码"
        (user/update-password! id update-password))

      (PUT "/:id/profile" []
        :return ::spec/Result
        :path-params [id :- int?]
        :body [user-profile user/profile-user]
        :summary "修改用户信息"
        (user/save-user-profile! id user-profile)))

    (context "/articles" []
      :tags ["articles"]
      ;; auth
      (GET "/" request
        :return ::spec/Result
        :summary "查看所有文章"
        (article/get-all-articles request))

      (POST "/" []
        :return ::spec/Result
        :body [article article/create-article]
        :summary "添加文章"
        (article/insert-article! article))

      (PUT "/:id" []
        :return ::spec/Result
        :body [article article/update-article]
        :summary "更新文章"
        (article/update-article! article))

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
        (article/upload-article! req)))

    (context "/tags" []
      :tags ["tags"]
      (POST "/" [tag]
        :return ::spec/Result
        ;:body [tag]
        :summary "标签"
        (tag/insert-tag! tag)))

    (context "/comments" []
      :tags ["comments"]
      (POST "/" []
        :return ::spec/Result ))))




