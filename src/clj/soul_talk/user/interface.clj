(ns soul-talk.user.interface
  (:require [soul-talk.user.handler :as handler]
            [soul-talk.user.spec :as spec]))

(def login spec/login)
(def register spec/register)
(def update-user spec/update-user)
(def user spec/user)
(def visible-user spec/visible-user)
(def update-password spec/update-password)
(def profile-user spec/profile-user)

(defn login! [req user]
  (handler/login! req user))

(defn logout! []
  (handler/logout!))

(defn register! [req user]
  (handler/register! req user))

(defn load-users []
  (handler/load-users))

(defn update-password! [user]
  (handler/update-password! user))

(defn save-user-profile! [user]
  (handler/save-user-profile! user))
