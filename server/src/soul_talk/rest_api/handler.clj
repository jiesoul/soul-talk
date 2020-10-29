(ns soul-talk.rest-api.handler
  (:require [compojure.api.sweet :refer :all]
            [soul-talk.article.interface :as article]
            [soul-talk.comment.interface :as comment]
            [soul-talk.spec.core :as spec]
            [soul-talk.tag.interface :as tag]
            [soul-talk.user.interface :as user]))

(def public-routes
  (routes
    (context "" []
      :tags ["登陆"]
     (POST "/login" req
       :return spec/Result
       :body [user user/login]
       :summary "用户登陆，登陆成功返回 Token"
       (user/login! req user))

      (POST "/logout" []
        :return spec/Result
        :summary "用户登出"
        (user/logout!))

      ;(POST "/register" req
      ;  :return spec/Result
      ;  :body [user user/register]
      ;  :summary "注册新用户"
      ;  (user/register! req user))
      )

    (context "/users" []
      :tags ["用户"]
      (GET "/:id/profile" []
        :return spec/Result
        :path-params [id :- int?]
        :summary "查看个人信息"
        (user/get-user-profile id)))

    (context "/articles" []
      :tags ["文章"]
      (GET "/public" req
        :return spec/Result
        :summary "查看所有发布的文章"
        (article/get-publish-article req))
      (GET "/archives" []
        :return spec/Result
        :summary "查看发布文章的存档"
        (article/get-article-archives))

      (GET "/archives/:year/:month" []
        :return spec/Result
        :path-params [year :- int? month :- int?]
        :summary "按年月查看发布存档"
        (article/get-article-archives-year-month year month))

      (GET "/:id" [id]
        :return spec/Result
        :summary "查看文章"
        (article/get-article id))

      (GET "/:id/tags" []
        :path-params [id :- string?]
        :return spec/Result
        :summary "获取文章标签"
        (tag/get-tags-by-article-id id))

      (GET "/:id/comments" [id]
        :return spec/Result
        :summary "文章评论"
        (comment/get-comments-by-articleId id)))

    (context "/tags" []
      :tags ["标签"]
      (GET "/" []
        :return spec/Result
        :summary "标签"
        (tag/get-all-tags)))

    ;(context "/comments" []
    ;  :tags ["comments"])
    ))

(def private-routes
  (routes
    :auth-rules user/authenticated
    (context "/users" []
      :tags ["用户"]
      (GET "/" []
        :return spec/Result
        :summary "查看所有用户"
        (user/load-users))

      (PUT "/:id/password" []
        :return spec/Result
        :path-params [id :- int?]
        :body [update-password user/update-password]
        :summary "更改用户密码"
        (user/update-password! id update-password))

      (PUT "/:id/profile" []
        :path-params [id :- int?]
        :body [user-profile user/profile-user]
        :return spec/Result
        :summary "修改用户信息"
        (user/save-user-profile! id user-profile)))

    (context "/articles" []
      :tags ["文章"]
      ;; auth
      (GET "/" request
        :return spec/Result
        :summary "查看所有文章"
        (article/get-all-articles request))

      (POST "/" []
        :return spec/Result
        :body [article article/create-article]
        :summary "添加文章"
        (article/insert-article! article))

      (PUT "/:id" []
        :return spec/Result
        :body [article article/update-article]
        :summary "更新文章"
        (article/update-article! article))

      (DELETE "/:id" [id]
        :return spec/Result
        :summary "删除文章"
        (article/delete-article! id))

      (PUT "/:id/publish" []
        :path-params [id :- string?]
        :return spec/Result
        :summary "发布文章"
        (article/publish-article! id))

      (POST "/upload" req
        :return spec/Result
        :summary "上传文章"
        (article/upload-article! req)))

    (context "/tags" []
      :tags ["标签"]
      (POST "/" []
        :body [tag tag/tag]
        :return spec/Result
        :summary "添加标签"
        (tag/insert-tag! tag))

      (DELETE "/:id" []
        :path-params [id :- int?]
        :return spec/Result
        :summary "删除标签"
        (tag/delete-tag! id)))

    (context "/comments" []
      :tags ["评论"]
      (POST "/" []
        :return spec/Result))))