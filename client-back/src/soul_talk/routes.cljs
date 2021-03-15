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
  (if (logged-in?)
    (doseq [event events]
      (dispatch event))
    (dispatch [:add-login-event events])))


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

(defroute "/data-dices" []
  (run-events-admin [[:set-breadcrumb ["基础数据" "数据字典" "查询"]]
                     [:data-dices/clean]
                     [:set-active-page :data-dices]]))

(defroute "/data-dices/new" []
  (run-events-admin [[:set-breadcrumb ["基础数据" "数据字典" "新增"]]
                     [:data-dices/clean]
                     [:set-active-page :data-dices/new]]))

(defroute "/data-dices/:id/edit" [id]
  (run-events-admin [[:set-breadcrumb ["基础数据" "数据字典" "编辑"]]
                     [:data-dices/load-data-dic id]
                     [:set-active-page :data-dices/edit]]))

(defroute "/menus" []
  (run-events-admin [[:set-breadcrumb ["基础数据" "菜单列表" "查询"]]
                     [:menus/init]
                     [:set-active-page :menus]]))

(defroute "/menus/new" []
  (run-events-admin [[:set-breadcrumb ["基础数据" "菜单管理" "添加菜单"]]
                     [:set-active-page :menus/new]]))

(defroute "/menus/:id/edit" [id]
  (run-events-admin [[:set-breadcrumb ["基础数据" "菜单管理" "修改菜单"]]
                     [:menus/load-menu id]
                     [:set-active-page :menus/edit]]))

(defroute "/roles" []
  (run-events-admin [[:set-breadcrumb ["基础数据" "角色列表" "查询"]]
                     [:roles/init]
                     [:set-active-page :roles]]))

(defroute "/roles/new" []
  (run-events-admin [[:set-breadcrumb ["基础数据" "角色管理" "添加角色"]]
                     [:menus/load-all]
                     [:set-active-page :roles/new]]))

(defroute "/roles/:id/edit" [id]
  (run-events-admin [[:set-breadcrumb ["基础数据" "角色管理" "修改角色"]]
                     [:roles/load-role id]
                     [:menus/load-all]
                     [:set-active-page :roles/edit]]))

(defroute "/users" []
  (run-events-admin [[:set-breadcrumb ["用户管理" "用户列表"]]
                     [:users/init]
                     [:set-active-page :users]]))

(defroute "/users/new" []
  (run-events-admin [[:set-breadcrumb ["用户管理" "个人管理" "新增"]]
                     [:users/init]
                     [:set-active-page :users/new]]))

(defroute "/users/:id/edit" [id]
  (run-events-admin [[:set-breadcrumb ["用户管理" "个人管理" "编辑"]]
                     [:users/init]
                     [:users/load-user id]
                     [:set-active-page :users/edit]]))

(defroute "/users/:id/password" [id]
  (run-events-admin [[:set-breadcrumb ["用户管理" "个人管理" "修改密码"]]
                     [:users/load-user id]
                     [:set-active-page :users/password]]))

(defroute "/users/:id/profile" [id]
  (run-events-admin [[:set-breadcrumb ["用户管理" "个人管理" "个人信息"]]
                     [:users/load-user id]
                     [:set-active-page :users/profile]]))

(defroute "/tags" []
  (run-events-admin [[:tags/load-all]
                     [:set-breadcrumb ["基础数据" "标签列表"]]
                     [:set-active-page :tags]]))

(defroute "/tags/new" []
  (run-events-admin [[:tags/load-all]
                     [:set-breadcrumb ["基础数据" "标签列表"]]
                     [:set-active-page :tags]]))

(defroute "/tags/:id/edit" [id]
  (run-events-admin [[:tags/load-all]
                     [:set-breadcrumb ["基础数据" "标签列表"]]
                     [:set-active-page :tags]]))

(defroute "/series" []
  (run-events-admin [[:series/load-all]
                     [:set-breadcrumb ["基础数据" "系列列表"]]
                     [:set-active-page :series]]))

(defroute "/series/new" []
  (run-events-admin [[:set-breadcrumb ["基础数据" "系列列表" "新增"]]
                     [:set-active-page :series/new]]))

(defroute "/series/:id/edit" [id]
  (run-events-admin [[:series/load-series id]
                     [:set-breadcrumb ["基础数据" "系列列表" "编辑"]]
                     [:set-active-page :series/edit]]))

(defroute "/app-keys" []
  (run-events-admin [[:app-keys/load-all]
                      [:set-breadcrumb ["基础数据" "app key 管理"]]
                      [:set-active-page :app-keys]]))

(defroute "/app-keys/new" []
  (run-events-admin [[:app-keys/load-all]
                     [:set-breadcrumb ["基础数据" "app key 管理" "新增"]]
                     [:set-active-page :app-keys]]))

(defroute "/app-keys/:id/edit" [id]
  (run-events-admin [[:app-keys/load-all]
                     [:set-breadcrumb ["基础数据" "app key 管理" "编辑"]]
                     [:set-active-page :app-keys]]))

(defroute "/articles" []
  (run-events-admin [[:set-breadcrumb ["文章管理" "文章查询"]]
                     [:articles/init]
                     [:set-active-page :articles]]))

(defroute "/articles/new" []
  (run-events-admin [[:set-breadcrumb ["文章管理" "添加文章"]]
                     [:articles/clear-edit]
                     [:set-active-page :articles/new]]))

(defroute "/articles/:id/edit" [id]
  (run-events-admin [[:set-breadcrumb ["文章管理" "添加文章"]]
                     [:articles/load-article id]
                     [:set-active-page :articles/edit]]))

(defroute "/articles/:id" [id]
  (run-events-admin [[:load-article id]
                     [:set-active-page :articles/view]]))

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