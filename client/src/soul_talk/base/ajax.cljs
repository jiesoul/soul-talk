(ns soul-talk.base.ajax
  (:require [ajax.core :as ajax]
            [re-frame.core :as rf]))

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
