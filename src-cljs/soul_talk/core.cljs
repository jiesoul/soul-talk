(ns soul-talk.core)

(defn main []
  (enable-console-print!)
  (prn "Hello, Clojurescript"))

(main)

(defn validate-form []
  (let [email (.getElementById js/document "email")
        password (.getElementById js/document "password")]
    (if (and (> (count (.-value email)) 0)
             (> (count (.-value password)) 0))
      true
      (do (js/alert "email和密码不能为空")
          false))))

(defn init []
  (if (and js/document
           (.-getElementByID js/document))
    (let [login-form (.getElementById js/document "loginForm")]
      (set! (.-onsubmit login-form) validate-form))))

(set! (.-onload js/window) init)