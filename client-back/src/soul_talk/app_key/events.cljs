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
  :app-key/load-page-ok
  (fn [db [_ {:keys [app-keys pagination query-params]}]]
    (assoc db :app-key/list app-keys :app-key/pagination pagination :app-key/query-params query-params)))

(reg-event-fx
  :app-key/load-page
  (fn [_ params]
    {:http {:method        GET
            :url           (str site-uri "/app-keys")
            :ajax-map      {:params params}
            :success-event [:app-key/load-page-ok]}}))

(reg-event-db
 :app-key/gen-ok
 (fn [db [_ {:keys [token]}]]
   (assoc-in db [:app-key/edit :token] token)))

(reg-event-fx
 :app-key/gen
 (fn [_ _]
   {:http {:method GET
           :url (str site-uri "/app-keys/gen")
           :success-event [:app-key/gen-ok]}}))

(reg-event-db
  :app-key/load-ok
  (fn [db [_ {:keys [app-key]}]]
    (assoc db :app-key/edit app-key)))

(reg-event-fx
  :app-key/load
  (fn [_ [_ id]]
    {:http {:method GET
            :url (str site-uri "/app-keys/" id)
            :success-event [:app-key/load-ok]}}))

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
  (fn [db [_ {:keys [app-key]}]]
    (assoc db :success "保存成功")))

(reg-event-fx
  :app-key/save
  (fn [_ [_ {:keys [app_name] :as tag}]]
    (if (str/blank? app_name)
      {:dispatch [:set-error "名称不能为空"]}
      {:http {:method        POST
              :url           (str site-uri "/app-keys")
              :ajax-map      {:params tag}
              :success-event [:set-success "保存成功"]}})))

(reg-event-db
  :app-key/update-ok
  (fn [db [_ {:keys [message body]}]]
    (assoc db :success "保存成功")))

(reg-event-fx
  :app-key/update
  (fn [_ [_ {:keys [app_name] :as app-key}]]
    (if (str/blank? app_name)
      {:dispatch [:set-error "名称不能为空"]}
      {:http {:method        PATCH
              :url           (str site-uri "/app-keys")
              :ajax-map      {:params app-key}
              :success-event [:set-success "保存成功"]}})))

(reg-event-fx
  :app-key/delete-ok
  (fn [{:keys [db]} [_ id]]
    (let [app-keys (:app-key/list db)
          app-keys (remove #(= id (:id %)) app-keys)]
      {:db (assoc db :app-key/list app-keys)
       :dispatch [:set-success "删除成功"]})))

(reg-event-fx
  :app-key/delete
  (fn [_ [_ id]]
    {:http {:method  DELETE
            :url (str site-uri "/app-keys/" id)
            :success-event [:app-key/delete-ok id]}}))