(ns soul-talk.interceptors
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [ajax.core :as ajax]
            [soul-talk.db :refer [api-key]]))

(defn request-headers [request]
  (-> request
    (update
      :headers
      #(merge
         %
         {:Accept  "application/transit+json"
          :api-key api-key}))))

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

