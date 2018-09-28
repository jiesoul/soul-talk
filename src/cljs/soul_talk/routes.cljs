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
  (let [pagination {:page 1
                    :pre-page 3}]
    (run-events [[:load-categories]
                 [:load-tags]
                 [:load-posts pagination]
                 [:load-posts-archives]
                 [:set-active-page :home]])))

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
  "/categories" []
  (run-events [[:load-categories]
               [:set-active-page :categories]]))

(secretary/defroute
  "/categories/add" []
  (run-events [[:set-active-page :categories/add]]))


(secretary/defroute
  "/posts" []
  (run-events [[:admin/load-posts]
               [:set-active-page :posts]]))

(secretary/defroute
  "/posts/archives/:year/:month" [year month]
  (run-events [[:load-posts-archives-year-month year month]
               [:set-active-page :posts/archives]]))

(secretary/defroute
  "/posts/add" []
  (run-events [[:load-categories]
                [:load-tags]
                [:set-active-page :posts/add]]))

(secretary/defroute
  "/posts/:id/edit" [id]
  (run-events [[:load-categories]
                [:load-tags]
                [:load-post id]
                [:set-active-page :posts/edit]]))

(secretary/defroute
  "/posts/:id" [id]
  (run-events [[:load-categories]
               [:load-tags]
               [:load-post id]
               [:set-active-page :posts/view]]))

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