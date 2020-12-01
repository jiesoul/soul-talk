(ns soul-talk.core
  (:require [reagent.core :as r]
            [reagent.dom :as rd]
            [soul-talk.common.interceptors :refer [load-interceptors!]]
            [soul-talk.routes :refer [hook-browser-navigation! logged-in? navigate!]]
            [re-frame.core :refer [dispatch-sync dispatch]]
            ;;初始化处理器和订阅器
            [soul-talk.common.coeffects]
            [soul-talk.common.effects]
            [soul-talk.routes]
            [soul-talk.events]
            [soul-talk.subs]
            [soul-talk.views]))

;; 挂载页面组件
(defn mount-component []
  (rd/render [#'soul-talk.views/main-page]
            (js/document.getElementById "app")))

;; 初始化方法
(defn init! []
  (dispatch-sync [:initialize-db])
  (dispatch-sync [:site-info/load 1])
  (when (logged-in?) (dispatch [:run-login-events]))
  (load-interceptors!)
  (hook-browser-navigation!)
  (mount-component))