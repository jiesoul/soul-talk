(ns soul-talk.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.util.response :as resp]
            [compojure.core :refer [routes GET defroutes]]
            [ring.middleware.defaults :refer :all]
            [compojure.route :as route]))

(defn home-handle [request]
  (resp/response (str "<html><body>" (:remote-addr request) "</body></html>")))

(def app-routes
  (routes
    (GET "/" request (home-handle request))
    (GET "/about" [] (str "这是关于我的页面"))
    (route/not-found "<h1>Page not found</h1>")))

(defn wrap-nocache [handler]
  (fn [request]
    (-> request
        handler
        (assoc-in [:headers "Pragma"] "no-cache"))))

(def app
  (-> app-routes
      (wrap-nocache)
      (wrap-reload)
      ;(wrap-defaults site-defaults)
      ))

(defn -main []
  (jetty/run-jetty
    app
    {:port 3000
     :join? false}))