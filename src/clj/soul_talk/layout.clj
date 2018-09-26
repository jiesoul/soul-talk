(ns soul-talk.layout
  (:require [selmer.parser :as parser]
            [ring.util.http-response :refer [content-type ok]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [clojure.java.io :as io]))

(parser/cache-off!)
(parser/set-resource-path! (io/resource "templates"))
(declare ^:dynamic *identity*)
(declare ^:dynamic *app-context*)
(parser/add-tag! :csrf-field (fn [_ _] (anti-forgery-field)))

(def timestamp (.getTime (java.util.Date.)))

(defn render
  " Additional information that is called csrf-token was in the html render"
  [template & [params]]
  (content-type
    (ok
      (parser/render-file
        template
        (assoc params
          :page template
          :user *identity*
          :timestamp timestamp
          ;:servlet-context *app-context*
          :csrf-token *anti-forgery-token*)))
    "text/html; charset=utf-8"))

(defn error-page
  [error-details]
  {:status (:status error-details)
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body (parser/render-file "error.html" error-details)})