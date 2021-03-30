(ns soul-talk.menu.events
  (:require [re-frame.core :as rf :refer [reg-event-db reg-event-fx]]
            [ajax.core :refer [GET POST DELETE PUT PATCH]]
            [clojure.string :as str]
            [soul-talk.db :refer [site-uri]]))

(rf/reg-event-db
  :menu/load-all-ok
  (fn [db [_ {:keys [menus]}]]
    (assoc db :menus menus)))

(rf/reg-event-fx
  :menu/load-all
  (fn []
    {:http {:method GET
            :url (str site-uri "/menus/all")
            :success-event [:menu/load-all-ok]}}))

(rf/reg-event-db
  :menu/load-menus-ok
  (fn [db [_ {:keys [menus]}]]
    (assoc db :menus menus)))

(rf/reg-event-fx
  :menu/load-menus
  (fn [_ [_ ids]]
    {:http {:method GET
            :url (str site-uri "/menus?ids=" ids)
            :success-event [:menu/load-menus-ok]}}))

(reg-event-db
  :menu/set-delete-dialog
  (fn [db [_ value]]
    (assoc db :menu/delete-dialog value)))

(reg-event-db
  :menu/set-query-params
  (fn [db [_ key value]]
    (assoc-in db [:menu/query-params key] value)))

(reg-event-db
  :menu/load-page-ok
  (fn [db [_ {:keys [menus pagination query-params]}]]
    (assoc db :menu/list menus :menu/pagination pagination :menu/query-params query-params)))

(reg-event-fx
  :menu/load-page
  (fn [_ [_ params]]
    {:http {:method        GET
            :url           (str site-uri "/menus")
            :ajax-map      {:params params}
            :success-event [:menu/load-page-ok]}}))

(reg-event-db
  :menu/load-menu-ok
  (fn [db [_ {:keys [menu]}]]
    (assoc db :menu/edit menu)))

(reg-event-fx
  :menu/load-menu
  (fn [_ [_ id]]
    {:http {:method GET
            :url (str site-uri "/menus/" id)
            :success-event [:menu/load-menu-ok]}}))

(reg-event-db
  :menu/set-attr
  (fn [db [_ attr]]
    (update-in db [:menu/edit] merge attr)))

(reg-event-fx
  :menu/save
  (fn [_ [_ menu]]
    {:http {:method        POST
            :url           (str site-uri "/menus")
            :ajax-map      {:params menu}
            :success-event [:set-success "保存成功"]}}))

(reg-event-fx
  :menu/update
  (fn [_ [_ menu]]
    {:http {:method        PATCH
            :url           (str site-uri "/menus")
            :ajax-map      {:params menu}
            :success-event [:set-success "保存成功"]}}))

(reg-event-fx
  :menu/delete-ok
  (fn [{:keys [db]} [_ id]]
    (let [menus (:menu/list db)
          menus (remove #(= id (:id %)) menus)]
      {:db (assoc db :menu/list menus :menu/delete-dialog false)
       :dispatch [:set-success "删除成功"]})))

(reg-event-fx
  :menu/delete
  (fn [_ [_ id]]
    {:http {:method  DELETE
            :url (str site-uri "/menus/" id)
            :success-event [:menu/delete-ok id]}}))

(reg-event-db
  :menu/init
  (fn [db _]
    (-> db
      (dissoc :menu/list :menu/query-params :menu/edit :menu/pagination)
      (assoc :menu/delete-dialog false))))

(reg-event-db
  :menu/select
  (fn [db [_ menu]]
    (assoc db :menu/selected menu)))
