(ns soul-talk.routes
  (:require [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [secretary.core :as secretary]
            [accountant.core :as accountant]
            [re-frame.core :refer [dispatch dispatch-sync subscribe]]
            [taoensso.timbre :as log])
  (:import goog.history.Html5History))

;; 判断是否登录
(defn logged-in? []
  @(subscribe [:user]))

;; 加载多个事件
(defn run-events
  [events]
  (doseq [event events]
    (do (log/info event)
        (dispatch event))))

(defn run-events-admin
  [events]
  (doseq [event events]
    (if (logged-in?)
      (dispatch event)
      (dispatch [:add-login-event event]))))

;; home 的默认加载
(defn home-page-events [& events]
  (.scrollTo js/window 0 0)
  (run-events-admin (into
                [[:set-active-page :admin]]
                events)))

;; 首页
(secretary/defroute
  "/" []
  (run-events [[:set-active-page :home]]))

;; 后台管理
(secretary/defroute
  "/admin" []
  (run-events [[:set-active-page :admin]]))

(secretary/defroute
  "/change-pass" []
  (run-events [[:set-active-page :change-pass]]))

(secretary/defroute
  "/user-profile" []
  (run-events [[:set-active-page :user-profile]]))

(secretary/defroute
  "/users" []
  (run-events [[:admin/load-users]
               [:set-active-page :users]]))

(secretary/defroute
  "/posts-add" []
  (run-events [[:load-categories]
                [:load-tags]
                [:set-active-page :posts/add]]))

(secretary/defroute
  "/posts" []
  (run-events [[:admin/load-posts]
                [:set-active-page :posts]]))

(secretary/defroute
  "/categories-add" []
  (run-events [[:set-active-page :categories/add]]))


;; 使用浏览器可以使用前进后退 历史操作
(defn hook-browser-navigation! []
  (doto
    (Html5History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true))
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))
     :reload-same-path? true})
  (accountant/dispatch-current!))