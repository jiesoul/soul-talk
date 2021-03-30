(ns soul-talk.routes
  (:require [goog.events :as events]
            [secretary.core :as secretary :refer-macros [defroute]]
            [accountant.core :as accountant]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch dispatch-sync  subscribe]])
  (:import [goog History]
           [goog.History EventType]))

;; 判断是否登录

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
  (doseq [event events]
    (dispatch event)))

;; 首页
(defroute "/" []
  (run-events
    [[:set-active-page :home]]))

(defroute "/tags" []
  (run-events [[:tags/load-all]
                     [:set-active-page :tags]]))

(defroute "/series" []
  (run-events [[:series/load-all]
               [:set-active-page :series]]))

(defroute "/articles" []
  (run-events [[:articles/init]
               [:set-active-page :articles]]))

(defroute "/articles/:id" [id]
  (run-events [[:load-article id]
               [:set-active-page :articles/view]]))

(defroute "/about" []
  (run-events [[:articles/init]
               [:set-active-page :about]]))

(defroute "*" []
  (run-events [[:set-active-page :home]]))

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