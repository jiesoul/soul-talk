(ns soul-talk.common.interceptors
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [ajax.core :as ajax]))

(defn request-headers [request]
  (let [token (rf/subscribe [:auth-token])]
    (-> request
      (update
        :headers
        #(merge
           %
           {:Accept        "application/transit+json"
            :Authorization (str "Token " @token)
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

