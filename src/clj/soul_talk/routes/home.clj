(ns soul-talk.routes.home
  (:require [compojure.core :refer (defroutes GET)]
            [taoensso.timbre :as log]))

(defn home-page []
  )

(defroutes
  home-routes
  (GET "/" [] (home-page))
  (GET "/login" [] (home-page))
  (GET "/register" [] (home-page))
  (GET "/posts/archives/:year/:month" [year month] (home-page))
  (GET "/posts/:id" [] (home-page)))


(defroutes
  auth-routes
  (GET "/admin" [] (home-page))
  (GET "/users" [] (home-page))
  (GET "/change-pass" [] (home-page))
  (GET "/user-profile" [] (home-page))
  (GET "/posts" [] (home-page))
  (GET "/posts/add" [] (home-page))
  (GET "/posts/:id/edit" [id] (home-page))
  (GET "/categories" [] (home-page))
  (GET "/categories/add" [] (home-page))
  (GET "/categories/:id/edit" [id] (home-page)))