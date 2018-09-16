(ns soul-talk.routes.home
  (:require [soul-talk.layout :as layout]
            [compojure.core :refer (defroutes GET)]
            [taoensso.timbre :as log]))

(defn home-page [req]
  (log/info req)
  (layout/render "home.html"))

(defroutes
  home-routes
  (GET "/" req (home-page req))
  (GET "/admin" req (home-page req)))