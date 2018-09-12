(ns soul-talk.core
  (:require [ring.adapter.jetty :as jetty]
            [compojure.core :refer [routes GET defroutes POST]]
            [compojure.route :as route]
            [selmer.parser :as parser]
            [soul-talk.routes.post :refer [post-routes]]
            [soul-talk.routes.services :refer [services-routes]]
            [soul-talk.middleware :refer [wrap-base]]
            [taoensso.timbre :as log]
            [ring.util.http-response :as resp]
            [soul-talk.layout :as layout]))

(parser/cache-off!)

(defn home-handle [request]
  (layout/render "index.html" request))

(defn dash-page [req]
  (layout/render "dash.html" req))

(defn error-page [error-details]
  {:status (:status error-details)
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body (layout/render "error.html" error-details)})

(def app-routes
  (routes
    (GET "/" request (home-handle request))
    (GET "/dash" req (dash-page req))
    (GET "/about" [] (str "这是关于我的页面"))
    (route/resources "/")
    (resp/not-found {:result :error
                     :message "page not found"})))

(def app
  (-> (routes
        post-routes
        services-routes
        app-routes)
      (wrap-base)))

(defn -main []
  (jetty/run-jetty
    app
    {:port 3000
     :join? false}))