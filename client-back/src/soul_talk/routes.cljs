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
                     [:set-breadcrumb ["基础信息" "网站信息"]]
                     [:set-active-page :site-info]]))

(defroute "/data-dices" []
  (run-events-admin [[:set-breadcrumb ["数据字典管理"]]
                     [:data-dices/clean]
                     [:set-active-page :data-dices]]))

(defroute "/menus" []
  (run-events-admin [[:set-breadcrumb ["菜单管理"]]
                     [:menus/clean]
                     [:set-active-page :menus]]))

(defroute "/roles" []
  (run-events-admin [[:roles/init]
                     [:set-active-page :roles]]))

(defroute "/users" []
  (run-events-admin [[:set-breadcrumb ["用户管理" "用户列表"]]
                     [:users/init]
                     [:set-active-page :users]]))

(defroute "/users/:id/password" [id]
  (run-events-admin [[:set-breadcrumb ["个人管理" "修改密码"]]
                     [:users/load-user id]
                     [:set-active-page :users-password]]))

(defroute "/users/:id/profile" [id]
  (run-events-admin [[:set-breadcrumb ["个人管理" "个人信息"]]
                     [:users/load-user id]
                     [:set-active-page :users-profile]]))

(defroute "/tags" []
  (run-events-admin [[:tags/load-all]
                     [:set-breadcrumb ["标签"]]
                     [:set-active-page :tags]]))

(defroute "/series" []
  (run-events-admin [[:series/load-all]
                     [:set-breadcrumb ["系列"]]
                     [:set-active-page :series]]))

(defroute "/app-keys" []
  (run-events-admin [[:app-keys/load-all]
                      [:set-breadcrumb ["app key 管理"]]
                      [:set-active-page :app-keys]]))

(defroute "/articles" []
  (run-events-admin [[:load-articles]
                     [:set-breadcrumb ["文章"]]
                     [:set-active-page :articles]]))

(defroute "/articles/add" []
  (run-events-admin [[:load-tags]
                     [:set-active-page :articles-add]]))

(defroute "/articles/:id/edit" [id]
  (run-events-admin [[:articles/get id]
                     [:load-tags]
                     [:set-active-page :articles-edit]]))

(defroute "/articles/:id" [id]
  (run-events-admin [[:load-tags]
                     [:load-article id]
                     [:set-active-page :articles-view]]))

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