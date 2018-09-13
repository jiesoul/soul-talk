(ns soul-talk.core
  (:require [reagent.core :as r]
            [soul-talk.ajax :refer [load-interceptors!]]
            [soul-talk.routes :refer [hook-browser-navigation!]]
            [soul-talk.views :refer [main-page ui]]
            [domina :as dom]
            [re-frame.core :as rf]
    ;;初始化处理器和订阅器
            soul-talk.effects
            soul-talk.handlers
            soul-talk.subs)
  (:import goog.history.Html5History))

(defn mount-component []
  (rf/dispatch-sync [:initialize])
  (r/render [#'main-page]
            (dom/by-id "app")))

(defn ^:export init []
  (rf/dispatch-sync [:initialize-db])
  (load-interceptors!)
  (hook-browser-navigation!)
  (mount-component))