(ns soul-talk.app-key.events
  (:require [re-frame.core :refer [reg-event-db reg-event-fx subscribe]]
            [ajax.core :refer [GET POST PATCH DELETE PUT]]
            [soul-talk.db :refer [site-uri]]
            [soul-talk.routes :refer [navigate!]]
            [clojure.string :as str]))

(reg-event-db
  :app-key/init
  (fn [db _]
    (-> db
      (assoc :app-key/delete-dialog false)
      (dissoc :app-key/list :app-key/edit :app-key/query-params :app-key/pagination))))

(reg-event-db
  :app-key/set-delete-dialog
  (fn [db [_ value]]
    (assoc db :app-key/delete-dialog value)))

(reg-event-db
  :app-key/load-all-ok
  (fn [db [_ {:keys [app-keys pagination query-params]}]]
    (assoc db :app-key/list app-keys :app-key/pagination pagination :app-key/query-params query-params)))

(reg-event-fx
  :app-key/load-all
  (fn [_ params]
    {:http {:method        GET
            :url           (str site-uri "/app-keys")
            :ajax-map      {:params params}
            :success-event [:app-key/load-all-ok]}}))

(reg-event-db
  :app-key/clean-edit
  (fn [db _]
    (dissoc db :app-key/edit)))

(reg-event-db
  :app-key/set-attr
  (fn [db [_ attr]]
    (update-in db [:app-key/edit] merge attr)))

(reg-event-db
  :app-key/save-ok
  (fn [db [_ {:keys [message body]}]]
    (assoc db :success "add a tag ok")))

(reg-event-fx
  :app-key/save
  (fn [_ [_ {:keys [name] :as tag}]]
    (if (str/blank? name)
      {:dispatch [:set-error "名称不能为空"]}
      {:http {:method        POST
              :url           (str site-uri "/app-keys")
              :ajax-map      {:params tag}
              :success-event [:app-key/save-ok]}})))

(reg-event-db
  :app-key/update-ok
  (fn [db [_ {:keys [message body]}]]
    (assoc db :success "add a tag ok")))

(reg-event-fx
  :app-key/save
  (fn [_ [_ {:keys [name] :as tag}]]
    (if (str/blank? name)
      {:dispatch [:set-error "名称不能为空"]}
      {:http {:method        POST
              :url           (str site-uri "/app-keys")
              :ajax-map      {:params tag}
              :success-event [:app-key/update-ok]}})))

(reg-event-db
  :app-key/delete-ok
  (fn [db [_ id]]
    (let [app-key (:app-key db)
          app-key (remove #(= id (:id %)) app-key)]
      (assoc db :success "删除成功" :app-key app-key))))

(reg-event-fx
  :app-key/delete
  (fn [_ [_ id]]
    {:http {:method  DELETE
            :url (str site-uri "/app-key/" id)
            :success-event [:app-key/delete-ok id]}}))