(ns soul-talk.routes
  (:require [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [secretary.core :as secretary]
            [accountant.core :as accountant]
            [re-frame.core :refer [dispatch dispatch-sync subscribe]])
  (:import goog.History))

;; 判断是否登录
(defn logged-in? []
  @(subscribe [:user]))

(defn href [url]
  {:href (str url)})

(defn navigate! [url]
  (accountant/navigate! url))

;; 加载多个事件
(defn run-events
  [events]
  (doseq [event events]
    (dispatch event)))

(defn run-events-admin
  [events]
  (js/console.log events)
  (doseq [event events]
    (if (logged-in?)
      (dispatch event)
      (dispatch [:add-login-event event]))))


(defn home-page-events [& events]
  (.scrollTo js/window 0 0)
  (run-events (into
                [[:load-categories]
                 [:load-tags]
                 [:set-active-page :home]]
                events)))

;; 首页
(secretary/defroute "/" []
  (let [pagination {:page     1
                    :pre-page 3}]
    (home-page-events
      [:load-posts pagination]
      [:load-posts-archives])))

(secretary/defroute "/login" []
  (run-events [[:set-active-page :login]]))

(secretary/defroute "/register" []
  (run-events [[:set-active-page :register]]))

;; 无登录下把事件加入登录事件
(defn admin-page-events [& events]
  (.scrollTo js/window 0 0)
  (run-events-admin (into
                      [[:set-active-page :admin]]
                      events)))

;; 后台管理
(secretary/defroute "/admin" []
  (run-events [[:set-active-page :admin]]))

(secretary/defroute "/change-pass" []
  (run-events [[:set-active-page :change-pass]]))

(secretary/defroute "/user-profile" []
  (run-events [[:set-active-page :user-profile]]))

(secretary/defroute "/users" []
  (run-events [[:admin/load-users]
                     [:set-active-page :users]]))

(secretary/defroute "/categories" []
  (run-events [[:load-categories]
                     [:set-active-page :categories]]))

(secretary/defroute "/categories/add" []
  (dispatch [:close-category])
  (run-events
    [[:set-active-page :categories/add]]))

(secretary/defroute "/categories/:id/edit" [id]
  (run-events [[:load-category id]
                     [:set-active-page :categories/edit]]))

(secretary/defroute "/categories/:id" [id]
  (run-events [[:load-category id]
                     [:set-active-page :categories/view]]))


(secretary/defroute "/posts" []
  (run-events [[:admin/load-posts]
                     [:set-active-page :posts]]))

(secretary/defroute "/posts/archives/:year/:month" [year month]
  (run-events [[:load-posts-archives-year-month year month]
                     [:set-active-page :posts/archives]]))

(secretary/defroute "/posts/add" []
  (run-events [[:load-categories]
                     [:load-tags]
                     [:set-active-page :posts/add]]))

(secretary/defroute "/posts/:id/edit" [id]
  (if-not (or (logged-in?)
            (nil? @(subscribe [:post])))
    (navigate! "/login")
    (run-events [[:load-post id]
                       [:load-categories]
                       [:set-active-page :posts/edit]])))

(secretary/defroute "/posts/:id" [id]
  (run-events [[:load-categories]
                     [:load-post id]
                     [:set-active-page :posts/view]]))

;; 使用浏览器可以使用前进后退 历史操作
(defn hook-browser-navigation! []
  (doto
    (History.)
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