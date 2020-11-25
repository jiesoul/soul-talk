(ns soul-talk.file.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
            [ajax.core :refer [POST GET]]
            [soul-talk.db :refer [site-uri]]))

(reg-event-fx
  :articles/upload-ok
  (fn [{:keys [db]} [_ {:keys [id] :as article}]]
    {:db (-> db
           )
     :dispatch-n (list [:set-success "保存成功"])}))

(reg-event-db
  :articles/upload-error
  (fn [_ [_ {:keys [message]}]]
    (js/alert message)))

(reg-event-fx
  :articles/upload
  (fn [_ [_ files]]
    (let [data (doto
                 (js/FormData.)
                 (.append "file" files))]
      {:http
       {:method   POST
        :url               (str site-uri "/articles/upload")
        :ajax-map          {:body data}
        :success-event [:articles/upload-ok]
        :error-event [:articles/upload-error]}})))

(reg-event-db
  :upload-md-file-ok
  (fn [db [_ {:keys [md]}]]
    (.val (js/$ "#editMdTextarea") md)
    (assoc db :upload/md md)))

(reg-event-db
  :upload-md-file-error
  (fn [_ [_ {:keys [message]}]]
    (js/alert message)))

(reg-event-fx
  :upload-md-file
  (fn [_ [_ files]]
    (let [data (doto
                 (js/FormData.)
                 (.append "file" files))]
      {:http
       {:method   POST
        :url               (str site-uri "/admin/files/md")
        :ajax-map          {:body data}
        :success-event [:upload-md-file-ok]
        :error-event [:upload-md-file-error]}})))