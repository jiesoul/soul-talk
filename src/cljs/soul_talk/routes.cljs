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
    (dispatch event)))

;; 后台加载判断是否登录
(defn run-events-admin
  [events]
  (log/info "user login ：" (not logged-in?))
  (doseq [event events]
    (if-not (logged-in?)
      (dispatch event)
      (dispatch [:add-login-event event])))
  (log/info @(subscribe [:login-events])))

;; home 的默认加载
(defn home-page-events [& events]
  (.scrollTo js/window 0 0)
  (run-events (into
                [[:set-active-page :home]]
                events)))

(defn admin-page-events [& events]
  (.scrollTo js/window 0 0)
  (run-events-admin (into
                      [[:set-active-page :admin]]
                      events)))
;;------------
;; 首页
(secretary/defroute
  "/" []
  (home-page-events))

;; 后台管理
(secretary/defroute
  "/admin" []
  (admin-page-events [:load-dashboard]))

(secretary/defroute
  "/register" []
  (run-events [[:set-active-page :register]]))

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
       (secretary/locate-route path))})
  (accountant/dispatch-current!))