(ns soul-talk.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.util.http-response :as resp]
            [compojure.core :refer [routes GET defroutes]]
            [ring.middleware.defaults :refer :all]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [selmer.parser :as parser]
            [ring.middleware.webjars :refer [wrap-webjars]]))

(defn home-handle [request]
  (parser/render-file "index.html" {:ip (:remote-addr request)}))

(defn error-page [error-details]
  {:status (:status error-details)
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body (parser/render-file "error.html" error-details)})

(def app-routes
  (routes
    (GET "/" request (home-handle request))
    (GET "/about" [] (str "这是关于我的页面"))
    (route/not-found error-page)))

(defn wrap-nocache [handler]
  (fn [request]
    (-> request
        handler
        (assoc-in [:headers "Pragma"] "no-cache"))))

(def app
  (-> app-routes
      (wrap-nocache)
      (wrap-reload)
      (wrap-webjars)
      ;(wrap-defaults site-defaults)
      ))

(defn -main []
  (jetty/run-jetty
    app
    {:port 3000
     :join? false}))