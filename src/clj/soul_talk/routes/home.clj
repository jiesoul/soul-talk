(ns soul-talk.routes.home
  (:require [soul-talk.layout :as layout]
            [compojure.core :refer (defroutes GET)]))

(defn home-page []
  (layout/render "home.html"))

(defn dash-page []
  (layout/render "dash.html"))

(defroutes
  home-routes
  (GET "/" [] (home-page))
  (GET "/dash" [] (home-page)))