(ns soul-talk.components.ajax
  (:require [ajax.core :as ajax :refer [default-headers]]))

(defn default-headers [request]
  (-> request
      (update
        :headers
        #(merge
           %
           {"Accept" "application/transit+json"
            "x-csrf-token" js/csrfToken}))))

(defn load-interceptors! []
  (swap! ajax/default-interceptors
         into
         [(ajax/to-interceptor {:name "defaults headers"
                                  :request default-headers})]))
