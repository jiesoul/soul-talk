(ns soul-talk.base.effects
  (:require [re-frame.core :as rf :refer [dispatch reg-fx reg-event-fx]]
            [accountant.core :as accountant]
            [soul-talk.base.local-storage :as storage]))

(reg-fx
 :http
 (fn [{:keys [method
              url
              success-event
              error-event
              ignore-response-body
              ajax-map]
       :or {error-event [:ajax-error]
            ajax-map {}}}]
   (dispatch [:set-loading])
   (method url (merge
                {:handler       (fn [response]
                                  (when success-event
                                    (dispatch (if ignore-response-body
                                                success-event
                                                (conj success-event response))))
                                  (dispatch [:unset-loading]))
                 :error-handler (fn [resp]
                                  (js/console.log resp)
                                  (dispatch (conj error-event resp))
                                  (dispatch [:unset-loading]))}
                ajax-map))))

(reg-fx
 :navigate
 (fn [url]
   (accountant/navigate! url)))

(reg-fx
 :reload-page
 (fn [_]
   (accountant/dispatch-current!)))

(reg-fx
 :set-user!
 (fn [user-identity]
   (storage/set-item! storage/login-user-key user-identity)))

(reg-fx
 :clean-user!
 (fn []
   (storage/remove-item! storage/login-user-key)))

(reg-fx
 :set-auth-token!
 (fn [auth-token]
   (storage/set-item! storage/auth-token-key auth-token)))

(reg-fx
 :clean-auth-token
 (fn []
   (storage/remove-item! storage/auth-token-key)))