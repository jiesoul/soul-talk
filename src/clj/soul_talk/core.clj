(ns soul-talk.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [compojure.core :refer [routes GET defroutes POST]]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.webjars :refer [wrap-webjars]]
            [compojure.route :as route]
            [selmer.parser :as parser]
            [ring.util.response :refer [redirect]]
            [soul-talk.auth-validate :as auth-validate]
            [ring.util.response :as res]
            [ring.middleware.format :as wrap-format]))

(parser/cache-off!)

(defn home-handle [request]
  (parser/render-file "index.html" request))

(defn error-page [error-details]
  {:status (:status error-details)
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body (parser/render-file "error.html" error-details)})

(defn login-page [request]
  (parser/render-file "login.html" request))

(defn handle-login [{:keys [params] :as request}]
  (let [email (:email params)
        password (:password params)]
    (cond
      (not (auth-validate/validate-email email)) (res/response {:status 400 :errors "Email不合法"})
      (not (auth-validate/validate-passoword password)) (res/response {:status 400 :errors "密码不合法"})
      (and (= email "jiesoul@gmail.com")
           (= password "12345678"))
      (do
        (assoc-in request [:session :identity] email)
        (res/response {:status :ok}))
      :else (res/response {:status 400 :errors "用户名密码不对"}))))

(defn handle-logout [request]
  (do
    (assoc request :session {})
    (redirect "/")))

(def app-routes
  (routes
    (GET "/" request (home-handle request))
    (GET "/login" request (login-page request))
    (POST "/login" req (handle-login req))
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
      (wrap-format/wrap-restful-format :formats [:json-kw])
      (wrap-defaults (assoc-in api-defaults [:security :anti-forgery] false))))

(defn -main []
  (jetty/run-jetty
    app
    {:port 3000
     :join? false}))