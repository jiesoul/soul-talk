(ns soul-talk.auth.interface
  (:require [soul-talk.auth.spec :as spec]
            [soul-talk.auth.handler :as handler]))

(def login spec/login)
(def register spec/register)
(def auth-token spec/auth-token)

(defn login! [req user]
  (handler/login! req user))

(defn logout! [auth-token]
  (handler/logout! auth-token))

(defn register! [req user]
  (handler/register! req user))

(defn auth-token [token]
  (handler/auth-token token))

(defn refresh-token [user-token]
  (handler/refresh-token user-token))
