(ns soul-talk.interceptors
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [ajax.core :as ajax]))

(defn request-headers [request]
  (let [login-token (:token @(rf/subscribe [:user]))]
    (-> request
      (update
        :headers
        #(merge
           %
           {:Accept        "application/transit+json"
            :Authorization (str "Token " login-token)
            :X-CSRF-Token  @(rf/subscribe [:csrf-token])})))))

(defn load-interceptors! []
  (swap! ajax/default-interceptors
    conj
    (ajax/to-interceptor {:name    "defaults headers"
                          :request request-headers})))

(def undos (r/atom ()))

(def undo-intercepto
  (rf/->interceptor
    :id :undo
    :before (fn [context]
              (swap! undos conj (-> context :coeffects :db))
              context)))

