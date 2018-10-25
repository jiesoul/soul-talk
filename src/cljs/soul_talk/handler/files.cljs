(ns soul-talk.handler.files
  (:require [re-frame.core :refer [reg-event-fx reg-event-db subscribe]]
            [ajax.core :refer [POST]]))

(reg-event-db
  :upload-md-file-ok
  (fn [_ [md]]
    ))

(reg-event-fx
  :upload-md-file
  (fn [_ [_ file]]
    (.log js/console file)
    {:http
     {:url (str "/api/admin/file/md")
      :ajax-map {:body file}
      :upload-md-file-ok [:upload-md-file-ok]}}))