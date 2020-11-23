(ns soul-talk.common.interceptors
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [ajax.core :as ajax]))

(defn request-headers [request]
  (let [login-token (rf/subscribe [:login-token])
        app-key (rf/subscribe [:app-key])]
    (-> request
      (update
        :headers
        #(merge
           %
           {:Accept        "application/transit+json"
            :Authorization (str login-token " " app-key)
            :X-CSRF-Token  @(rf/subscribe [:csrf-token])})))))

(defn load-interceptors! []
  (swap! ajax/default-interceptors
    conj
    (ajax/to-interceptor {:name    "defaults headers"
                          :request request-headers})))

(def undos (r/atom ()))

(def undo-interceptor
  (rf/->interceptor
    :id :undo
    :before (fn [context]
              (swap! undos conj (-> context :coeffects :db))
              context)))

