(ns soul-talk.core
  (:require [reagent.core :as r]
            [reagent.dom :as rd]
            [soul-talk.interceptors :refer [load-interceptors!]]
            [soul-talk.routes :refer [hook-browser-navigation! navigate!]]
            [re-frame.core :refer [dispatch-sync dispatch]]
            ;;初始化处理器和订阅器
            [soul-talk.coeffects]
            [soul-talk.effects]
            [soul-talk.routes]
            [soul-talk.subs]
            [soul-talk.events]
            [soul-talk.views]))

(set! *warn-on-infer* true)

;(def functional-compiler (reagent.core/create-compiler {:function-components true}))
;(r/set-default-compiler! functional-compiler)

;; 挂载页面组件
(defn mount-component []
  (rd/render [#'soul-talk.views/main-page]
            (js/document.getElementById "app")))

;; 初始化方法
(defn init! []
  (load-interceptors!)
  (hook-browser-navigation!)
  (mount-component)
  (dispatch-sync [:initialize-db])
  (dispatch-sync [:site-info/load 1]))