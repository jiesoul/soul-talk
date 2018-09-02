(ns soul-talk.login
  (:require [domina :refer [by-id value]]))

(defn validate-form []
  (let [email (by-id "email")
        password (by-id "password")]
    (if (and (> (count (value email)) 0)
             (> (count (value password)) 0))
      true
      (do
        (js/alert "email和密码不能为空")
        false))))

(defn ^:export init []
  (if (and js/document
           (.-getElementById js/document))
    (let [login-form (by-id "loginForm")]
      (set! (.-onsubmit login-form) validate-form))))

;(set! (.-onload js/window) init)