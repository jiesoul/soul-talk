(ns soul-talk.routes
  (:require [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [secretary.core :as secretary]
            [accountant.core :as accountant]
            [re-frame.core :refer [dispatch dispatch-sync subscribe]]
            [taoensso.timbre :as log])
  (:import goog.history.Html5History))

;; 加载多个事件
(defn run-events
  [events]
  (doseq [event events]
    (dispatch event)))

;; home 的默认加载
(defn home-page-events [& events]
  (.scrollTo js/window 0 0)
  (run-events (into
                [[:set-active-page :home]]
                events)))
;; 判断是否登录
(defn logged-in? []
  @(subscribe [:user]))

;;admin 默认加载 这里需要判断登录
;(defn admin-page-events [& events]
;  (.scrollTo js/window 0 0)
;  (if (logged-in?)
;    (run-events (into
;                  [[:set-active-page :admin]]
;                  events))
;    (dispatch [:add-login-event events])))

;;------------
;; 首页
(secretary/defroute
  "/" []
  (log/info "load home")
  (home-page-events))

;; 后台管理
(secretary/defroute
  "/admin" []
  (log/info "load admin")
  (run-events [[:set-active-page :admin]]))

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