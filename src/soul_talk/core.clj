(ns soul-talk.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]
            [ring.middleware.reload :refer [wrap-reload]]
            [compojure.core :refer [routes GET]]))

(defn response-handler [request]
  (response/response
    (str "<html><body>your IP is "
                 (:remote-addr request)
                 "</body></html>")))

(def handler
  (routes
    (GET "/" request response-handler)
    (GET "/about" [] (str "这是关于我的页面"))))

(defn wrap-nocache [handler]
  (fn [request]
    (-> request
        handler
        (assoc-in [:headers "Pragma"] "no-cache"))))

(def app
  (-> handler
      wrap-nocache
      wrap-reload))

(defn -main []
  (jetty/run-jetty
    app
    {:port 3000
     :join? false}))