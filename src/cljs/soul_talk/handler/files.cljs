(ns soul-talk.handler.files
  (:require [re-frame.core :refer [reg-event-fx reg-event-db subscribe]]
            [ajax.core :refer [POST]]
            [taoensso.timbre :as log]))

(reg-event-db
  :upload-md-file-ok
  (fn [_ [_ resp]]
    (js/alert resp)))

(reg-event-db
  :upload-md-file-error
  (fn [_ [_ md]]
    (js/alert md)))

(reg-event-fx
  :upload-md-file
  (fn [_ [_ files]]
    (do
      (log/info files)
      {:http
       {:method   POST
        :url               (str "/api/admin/files/md")
        :ajax-map          {:miltipart-params files}
        :success-event [:upload-md-file-ok]
        :error-event [:upload-md-file-error]}})))