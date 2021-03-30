(ns soul-talk.routes
  (:require [goog.events :as events]
            [secretary.core :as secretary :refer-macros [defroute]]
            [accountant.core :as accountant]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch dispatch-sync  subscribe]])
  (:import [goog History]
           [goog.History EventType]))

;; 判断是否登录
(defn logged-in? []
  @(subscribe [:user]))

(defn context-url [url]
  (str url))

(defn href [url]
  {:href (str url)})

(defn set-html! [el content]
  (aset el "innerHTML" content))

(defn navigate! [url]
  (accountant/navigate! (str "#" url)))

(defn run-events
  [events]
  (if (logged-in?)
    (doseq [event events]
      (dispatch event))
    (dispatch [:set-active-page :login])))

(defn run-events-admin
  [events]
  (let [events (conj events [:clean-error] [:clean-success])]
    (if (logged-in?)
      (doseq [event events]
        (dispatch event))
      (dispatch [:add-login-event events]))))

(defroute "/login" []
  (dispatch [:set-active-page :login]))

;; 首页
(defroute "/" []
  (run-events-admin
    [[:set-active-page :login]]))

;; 后台管理
(defroute "/dash" []
  (run-events-admin [[:set-breadcrumb ["统计面板"]]
                     [:set-active-page :dash]]))

(defroute "/site-info/:id" [id]
  (run-events-admin [[:site-info/load id]
                     [:set-breadcrumb ["基础数据" "网站信息" "编辑"]]
                     [:set-active-page :site-info]]))

(defroute "/data-dic" []
  (run-events-admin [[:set-breadcrumb ["基础数据" "数据字典" "查询"]]
                     [:data-dic/init]
                     [:set-active-page :data-dic]]))

(defroute "/data-dic/new" []
  (run-events-admin [[:set-breadcrumb ["基础数据" "数据字典" "新增"]]
                     [:data-dic/clean-edit]
                     [:set-active-page :data-dic/new]]))

(defroute "/data-dic/:id/edit" [id]
  (run-events-admin [[:set-breadcrumb ["基础数据" "数据字典" "编辑"]]
                     [:data-dic/load-data-dic id]
                     [:set-active-page :data-dic/edit]]))

(defroute "/menu" []
  (run-events-admin [[:set-breadcrumb ["基础数据" "菜单列表" "查询"]]
                     [:menu/init]
                     [:set-active-page :menu]]))

(defroute "/menu/new" []
  (run-events-admin [[:set-breadcrumb ["基础数据" "菜单管理" "添加菜单"]]
                     [:set-active-page :menu/new]]))

(defroute "/menu/:id/edit" [id]
  (run-events-admin [[:set-breadcrumb ["基础数据" "菜单管理" "修改菜单"]]
                     [:menu/load-menu id]
                     [:set-active-page :menu/edit]]))

(defroute "/role" []
  (run-events-admin [[:set-breadcrumb ["基础数据" "角色列表" "查询"]]
                     [:role/init]
                     [:set-active-page :role]]))

(defroute "/role/new" []
  (run-events-admin [[:set-breadcrumb ["基础数据" "角色管理" "添加角色"]]
                     [:menu/load-all]
                     [:set-active-page :role/new]]))

(defroute "/role/:id/edit" [id]
  (run-events-admin [[:set-breadcrumb ["基础数据" "角色管理" "修改角色"]]
                     [:role/load-role id]
                     [:menu/load-all]
                     [:set-active-page :role/edit]]))

(defroute "/user" []
  (run-events-admin [[:set-breadcrumb ["用户管理" "用户列表"]]
                     [:user/init]
                     [:set-active-page :user]]))

(defroute "/user/new" []
  (run-events-admin [[:set-breadcrumb ["用户管理" "个人管理" "新增"]]
                     [:user/init]
                     [:set-active-page :user/new]]))

(defroute "/user/:id/edit" [id]
  (run-events-admin [[:set-breadcrumb ["用户管理" "个人管理" "编辑"]]
                     [:user/init]
                     [:user/load-user id]
                     [:set-active-page :user/edit]]))

(defroute "/user/:id/password" [id]
  (run-events-admin [[:set-breadcrumb ["用户管理" "个人管理" "修改密码"]]
                     [:user/load-user id]
                     [:set-active-page :user/password]]))

(defroute "/user/:id/profile" [id]
  (run-events-admin [[:set-breadcrumb ["用户管理" "个人管理" "个人信息"]]
                     [:user/load-user id]
                     [:set-active-page :user/profile]]))

(defroute "/tag" []
  (run-events-admin [[:tag/init]
                     [:set-breadcrumb ["基础数据" "标签列表"]]
                     [:set-active-page :tag]]))

(defroute "/tag/new" []
  (run-events-admin [[:tag/clean-edit]
                     [:set-breadcrumb ["基础数据" "标签列表"]]
                     [:set-active-page :tag/new]]))

(defroute "/tag/:id/edit" [id]
  (run-events-admin [[:tag/load id]
                     [:set-breadcrumb ["基础数据" "标签列表"]]
                     [:set-active-page :tag/edit]]))

(defroute "/series" []
  (run-events-admin [[:series/init]
                     [:set-breadcrumb ["基础数据" "系列列表"]]
                     [:set-active-page :series]]))

(defroute "/series/new" []
  (run-events-admin [[:series/clean-edit]
                     [:set-breadcrumb ["基础数据" "系列列表" "新增"]]
                     [:set-active-page :series/new]]))

(defroute "/series/:id/edit" [id]
  (run-events-admin [[:series/load id]
                     [:set-breadcrumb ["基础数据" "系列列表" "编辑"]]
                     [:set-active-page :series/edit]]))

(defroute "/app-key" []
  (run-events-admin [[:app-key/init]
                      [:set-breadcrumb ["基础数据" "app key 管理"]]
                      [:set-active-page :app-key]]))

(defroute "/app-key/new" []
  (run-events-admin [[:app-key/clean-edit]
                     [:set-breadcrumb ["基础数据" "app key 管理" "新增"]]
                     [:set-active-page :app-key]]))

(defroute "/app-key/:id/edit" [id]
  (run-events-admin [[:app-key/load id]
                     [:set-breadcrumb ["基础数据" "app key 管理" "编辑"]]
                     [:set-active-page :app-key]]))

(defroute "/article" []
  (run-events-admin [[:set-breadcrumb ["文章管理" "文章查询"]]
                     [:article/init]
                     [:set-active-page :article]]))

(defroute "/article/new" []
  (run-events-admin [[:article/clear-edit]
                     [:set-breadcrumb ["文章管理" "添加文章"]]
                     [:set-active-page :article/new]]))

(defroute "/article/:id/edit" [id]
  (run-events-admin [[:set-breadcrumb ["文章管理" "添加文章"]]
                     [:article/load id]
                     [:set-active-page :article/edit]]))

(defroute "/article/:id" [id]
  (run-events-admin [[:load-article id]
                     [:set-active-page :article/view]]))

(defroute "*" []
  (run-events-admin [[:set-breadcrumb ["未找到页面"]]
               [:set-active-page :default]]))

(secretary/set-config! :prefix "#")

;; 使用浏览器可以使用前进后退 历史操作
(defn hook-browser-navigation! []
  (doto
    (History.)
    (events/listen EventType.NAVIGATE #(secretary/dispatch! (.-token %)))
    (.setEnabled true))
  (accountant/configure-navigation!
    {:nav-handler (fn [path]
                    (secretary/dispatch! path))
     :path-exists? (fn [path]
                     (secretary/locate-route path))
     :reload-same-path? true})
  (accountant/dispatch-current!))