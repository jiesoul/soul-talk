(ns soul-talk.common.effects
  (:require [re-frame.core :as rf :refer [dispatch reg-fx reg-event-fx reg-event-db]]
            [accountant.core :as accountant]))

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