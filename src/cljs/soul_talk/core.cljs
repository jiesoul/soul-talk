(ns soul-talk.core
  (:require [reagent.core :as r]
            [soul-talk.ajax :refer [load-interceptors!]]
            [soul-talk.routes :refer [hook-browser-navigation!]]
            [soul-talk.views :refer [main-page]]
            [re-frame.core :refer [dispatch-sync]]
    ;;初始化处理器和订阅器
            soul-talk.effects
            soul-talk.handlers
            soul-talk.subs)
  (:import goog.history.Html5History))

;; 挂载页面组件
(defn mount-component []
  (r/render [#'main-page]
            (.getElementById js/document "app")))

;; 初始化方法
(defn init! []
  (dispatch-sync [:initialize-db])
  (load-interceptors!)
  (hook-browser-navigation!)
  (mount-component))