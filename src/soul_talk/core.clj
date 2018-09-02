(ns soul-talk.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.util.http-response :as resp]
            [compojure.core :refer [routes GET defroutes POST]]
            [ring.middleware.defaults :refer :all]
            [compojure.route :as route]
            [selmer.parser :as parser]
            [ring.middleware.webjars :refer [wrap-webjars]]
            [ring.util.response :refer [redirect]]))

(parser/cache-off!)

(defn home-handle [request]
  (parser/render-file "index.html" request))

(defn error-page [error-details]
  {:status (:status error-details)
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body (parser/render-file "error.html" error-details)})

(defn login-page [request]
  (parser/render-file "login.html" request))

(defn handle-login [email password request]
  (if (and (= email "jiesoul@gmail.com")
           (= password "12345678"))
    (home-handle (assoc-in request [:session :identity] email))
    (login-page (assoc request :error "用户名密码不对"))))

(defn handle-logout [request]
  (do
    (assoc request :session {})
    (redirect "/")))

(def app-routes
  (routes
    (GET "/" request (home-handle request))
    (GET "/login" request (login-page request))
    (POST "/login" [email password :as req] (handle-login email password req))
    (GET "/logout" request (handle-logout request))
    (GET "/about" [] (str "这是关于我的页面"))
    (route/resources "/")
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
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      ))

(defn -main []
  (jetty/run-jetty
    app
    {:port 3000
     :join? false}))