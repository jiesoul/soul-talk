(ns soul-talk.interceptors
  (:require [reagent.core :as r]
            [re-frame.core :as rf]))

(def undos (r/atom ()))

(def undo-interceptor
  (rf/->interceptor
    :id :undo
    :before (fn [context]
              (swap! undos conj (-> context :coeffects :db))
              context)))

