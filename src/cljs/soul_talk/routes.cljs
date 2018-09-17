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
  (log/info "current home events: " events)
  (doseq [event events]
    (dispatch event)))

;; 后台加载判断是否登录
(defn run-events-admin
  [events]
  (log/info "current admin events: " events)
  (doseq [event events]
    (if (logged-in?)
      (dispatch event)
      (dispatch [:add-login-event event]))))

;; home 的默认加载
(defn home-page-events [& events]
  (.scrollTo js/window 0 0)
  (run-events (into
                [[:set-active-page :home]]
                events)))

;;后台管理默认加载
(defn admin-page-events [& events]
  (.scrollTo js/window 0 0)
  (run-events (into
                      [[:set-active-page :admin]]
                      events)))
;;------------
;; 首页
(secretary/defroute
  "/" []
  (home-page-events []))

;; 后台管理
(secretary/defroute
  "/admin" []
  (admin-page-events))

(secretary/defroute
  "/admin/users" []
  (admin-page-events [:set-active-page :users]))

;(secretary/defroute
;  "/admin/posts" []
;  (admin-page-events [:set-active-page :posts]))

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