(ns soul-talk.routes.auth
  (:require [soul-talk.models.user-db :as user-db]
            [ring.util.http-response :as resp]
            [buddy.hashers :as hashers]
            [taoensso.timbre :as log]
            [soul-talk.auth-validate :refer [reg-errors login-errors change-pass-errors]]
            [compojure.core :refer [defroutes POST GET]]
            [selmer.parser :as parser]
            [java-time.local :as l]))

(defn register! [{:keys [session] :as req} user]
  (if (reg-errors user)
    (resp/precondition-failed {:result :error})
    (try
      (if-let [temp-user (user-db/select-user (:email user))]
        (resp/internal-server-error {:result  :error
                                     :message (str (:email temp-user) " 已被注册")})
        (do
          (user-db/save-user!
            (-> user
                (dissoc :pass-confirm)
                (update :password hashers/encrypt)))
          (log/info "注册成功")
          (-> {:result :ok}
              (resp/ok))))
      (catch Exception e
        (do
          (log/error e)
          (resp/internal-server-error
            {:result :error
             :message "发生内部错误，请联系管理员"}))))))

(defn login! [{:keys [session] :as req} {:keys [email password] :as user}]
  (if (login-errors user)
    (resp/precondition-failed {:result :error})
    (try
      (let [db-user (user-db/select-user email)]
        (if-not (hashers/check password (:password db-user))
          (resp/unauthorized
            {:result :error
             :message "email或密码错误,登录失败"})
          (do
            (-> user
                (assoc :last-time (l/local-date-time))
                (user-db/update-login-time))
            (-> {:result :ok}
                (resp/ok)
                (assoc :session (assoc session :identity email))))))
      (catch Exception e
        (do
          (log/error e)
          (resp/internal-server-error
            {:result :error
             :message "发生内部错误，请联系管理员"}))))))

(defn logout! [request]
  (-> "/dash"
      resp/found
      (assoc :session nil)))

(defn change-pass! [{:keys [email pass-old pass-new] :as params}]
  (if (change-pass-errors params)
    (resp/precondition-failed {:result :error})
    (let [user (user-db/select-user email)]
      (if-not (hashers/check pass-old (:password user))
        (resp/unauthorized {:result  :error
                            :message "旧密码错误"})
        (do
          (-> params
              (assoc :pass-new (hashers/encrypt pass-new))
              (user-db/change-pass!))
          (resp/ok {:result :ok}))))))

(defroutes
  auth-routes
  (GET "/register" req (parser/render-file "register.html" req))
  (POST "/register" req (register! req (:params req)))
  (GET "/login" request (parser/render-file "login.html" request))
  (POST "/login" req (login! req (:params req)))
  (POST "/change-pass" req (change-pass! (:params req)))
  (GET "/logout" request (logout! request)))