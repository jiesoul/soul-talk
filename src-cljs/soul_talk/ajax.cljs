(ns soul-talk.ajax
  (:require [ajax.core :as ajax]))

(defn default-headers [request]
  (-> request
      (update
        :headers
        #(merge
           %
           {"Accept" "application/transit+json"
            "X-CSRF-Token" js/csrfToken}))))

(defn load-interceptors! []
  (swap! ajax/default-interceptors
         conj
         (ajax/to-interceptor {:name    "defaults headers"
                               :request default-headers})))
