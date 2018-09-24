(ns soul-talk.routes.home
  (:require [soul-talk.layout :as layout]
            [compojure.core :refer (defroutes GET)]
            [taoensso.timbre :as log]))

(defn home-page []
  (layout/render "home.html"))

(defroutes
  home-routes
  (GET "/" [] (home-page))
  (GET "/posts/:id" [id] (home-page))
  (GET "/test/js/test" [] (home-page)))


(defroutes
  auth-routes
  (GET "/admin" [] (home-page))
  (GET "/users" [] (home-page))
  (GET "/change-pass" [] (home-page))
  (GET "/user-profile" [] (home-page))
  (GET "/posts/add" [] (home-page))
  (GET "/posts" [] (home-page))
  (GET "/categories" [] (home-page))
  (GET "/categories/add" [] (home-page))
  )