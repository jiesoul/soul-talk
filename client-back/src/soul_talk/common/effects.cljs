(ns soul-talk.common.effects
  (:require [re-frame.core :as rf :refer [dispatch reg-fx reg-event-fx reg-event-db]]
            [accountant.core :as accountant]))

(reg-event-fx
  :ajax-error
  (fn [_ [_ {:keys [body status] :as resp}]]
    (let [{:keys [message]} body]
      (js/console.log "error response: " resp)
      {:dispatch-n (condp = status
                     0 (list [:set-error message])
                     400 (list [:set-error message])
                     401 (list [:set-error message] [:logout])
                     403 (list [:set-error message] [:logout])
                     404 (list [:set-error message])
                     500 (list [:set-error message])
                     (list [:set-error message]))})))

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

;; 响应事件
(defn query [db [event-id]]
  (event-id db))