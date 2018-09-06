(ns soul-talk.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [compojure.core :refer [routes GET defroutes POST]]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.webjars :refer [wrap-webjars]]
            [compojure.route :as route]
            [selmer.parser :as parser]
            [ring.util.response :refer [redirect]]
            [ring.middleware.format :as wrap-format]
            [soul-talk.routes.auth :refer [auth-routes]]
            [taoensso.timbre :as log]
            [ring.middleware.session :refer [wrap-session]]))

(parser/cache-off!)

(defn home-handle [request]
  (parser/render-file "index.html" request))

(defn error-page [error-details]
  {:status (:status error-details)
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body (parser/render-file "error.html" error-details)})

(def app-routes
  (routes
    (GET "/" request (home-handle request))
    (GET "/about" [] (str "这是关于我的页面"))
    (route/resources "/")
    (route/not-found error-page)))

(defn wrap-nocache [handler]
  (fn [request]
    (-> request
        handler
        (assoc-in [:headers "Pragma"] "no-cache"))))

(def app
  (-> (routes auth-routes app-routes)
      (wrap-nocache)
      (wrap-reload)
      (wrap-webjars)
      (wrap-format/wrap-restful-format :formats [:json-kw])
      (wrap-session)
      (wrap-defaults (assoc-in api-defaults [:security :anti-forgery] false))))

(defn -main []
  (jetty/run-jetty
    app
    {:port 3000
     :join? false}))