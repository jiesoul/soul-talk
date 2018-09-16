(ns soul-talk.effects
  (:require [re-frame.core :as rf :refer [dispatch reg-fx reg-event-fx]]
            [accountant.core :as accountant]
            [taoensso.timbre :as log]))


(reg-fx
  :http
  (fn [{:keys [method
               url
               success-event
               error-event
               ignore-response-body
               ajax-map]
        :or {:error-event [:ajax-error]
             ajax-map {}}}]
    (method url (merge
                  {:handler       (fn [response]
                                    (when success-event
                                      (dispatch (if ignore-response-body
                                                  success-event
                                                  (conj success-event response)))))
                   :error-handler (fn [error]
                                    (log/error "请求错误：" error)
                                    (dispatch (conj error-event error)))}
                  ajax-map))))


(reg-fx
  :navigate
  (fn [url]
    (accountant/navigate! url)))

(reg-fx
  :reload-page
  (fn []
    (accountant/dispatch-current!)))


(reg-fx
  :set-user!
  (fn [user-identity]
    (set! js/user (clj->js user-identity))))