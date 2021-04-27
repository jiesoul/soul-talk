(ns soul-talk.effects
  (:require [re-frame.core :as rf :refer [dispatch reg-fx reg-event-fx reg-event-db]]
            [accountant.core :as accountant]
            [ajax.core :as ajax]))

(reg-event-fx
  :ajax-error
  (fn [_ [_ {:keys [response status] :as resp}]]
    (let [message (:message response)]
      (js/console.log ".... error resp: " resp)
      {:dispatch-n (condp = status
                     400 (list [:set-error (str "错误的请求!" message)])
                     401 (list [:set-error (str "请示验证失败！ " message)] [:logout])
                     403 (list [:set-error (str "错误的请求！ " message)] [:logout])
                     404 (list [:set-error (str "所请求的资源未找到，请检查！" message)])
                     500 (list [:set-error (str "发生内部错误，请联系管理员或重试！") message])
                     (list [:set-error "发生未知错误！"]))})))

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
                 {:handler         (fn [response]
                                     (when success-event
                                       (dispatch (if ignore-response-body
                                                   success-event
                                                   (conj success-event response))))
                                     (dispatch [:unset-loading]))
                  :error-handler   (fn [resp]
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