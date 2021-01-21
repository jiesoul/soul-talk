(ns soul-talk.menu.events
  (:require [re-frame.core :as rf :refer [reg-event-db reg-event-fx]]
            [ajax.core :refer [GET POST DELETE PUT PATCH]]
            [clojure.string :as str]
            [soul-talk.db :refer [site-uri]]))

(rf/reg-event-fx
  :menus/load-menus-ok
  (fn [{:keys [db]} [_ {:keys [menus]}]]
    {:db (assoc db :menus  menus)}))

(rf/reg-event-fx
  :menus/load-menus
  (fn [_ [_ ids]]
    {:http {:method GET
            :url (str site-uri "/menus?ids=" ids)
            :success-event [:menus/load-menus-ok]}}))

(reg-event-db
  :menus/set-add-status 
  (fn [db [_ value]]
    (assoc db :menus/add-status value)))

(reg-event-db
  :menus/set-edit-status
  (fn [db [_ value]]
    (assoc db :menus/edit-status value)))

(reg-event-db
  :menus/set-delete-status
  (fn [db [_ value]]
    (assoc db :menus/delete-status value)))

(reg-event-db
  :menus/set-query-params
  (fn [db [_ key value]]
    (assoc-in db [:menus/query-params key] value)))

(reg-event-db
  :menus/clean-query-params
  (fn [db _]
    (js/console.log "clean query params")
    (dissoc db :menus/query-params)))

(reg-event-db
  :menus/load-page-ok
  (fn [db [_ {:keys [menus pagination query-params]}]]
    (assoc db :menus menus :menus/pagination pagination :menus/query-params query-params)))

(reg-event-fx
  :menus/load-page
  (fn [_ [_ params]]
    {:http {:method        GET
            :url           (str site-uri "/menus")
            :ajax-map      {:params params}
            :success-event [:menus/load-page-ok]}}))

(reg-event-db
  :menus/load-menu-ok
  (fn [db [_ {:keys [menu]}]]
    (assoc db :menus/edit menu)))

(reg-event-fx
  :menus/load-menu
  (fn [_ [_ id]]
    {:http {:method GET
            :url (str site-uri "/menus/" id)
            :success-event [:menus/load-menu-ok]}}))

(reg-event-db
  :menus/clean-edit
  (fn [db _]
    (dissoc db :menus/edit)))

(reg-event-db
  :menus/set-attr
  (fn [db [_ key value]]
    (assoc-in db [:menus/edit key] value)))

(reg-event-db
  :menus/add-ok
  (fn [db [_ {:keys [menu]}]]
    (let [menus (:menus db)]
      (assoc db :success "保存成功" :menus (conj menus menu)))))

(reg-event-fx
  :menus/add
  (fn [_ [_ menu]]
    (println "menu: " menu)
    {:http {:method        POST
            :url           (str site-uri "/menus")
            :ajax-map      {:params menu}
            :success-event [:menus/add-ok]}}))

(reg-event-db
  :menus/update-ok
  (fn [db [_ {:keys [menu]}]]
    (assoc db :success "保存成功")))

(reg-event-fx
  :menus/update
  (fn [_ [_ menu]]
    {:http {:method        PATCH
            :url           (str site-uri "/menus")
            :ajax-map      {:params menu}
            :success-event [:menus/update-ok]}}))

(reg-event-db
  :menus/delete-ok
  (fn [db [_ id]]
    (let [menus (:menus db)
          menus (remove #(= id (:id %)) menus)]
      (assoc db :success "删除成功" :menus menus :menus/delete-status false))))

(reg-event-fx
  :menus/delete
  (fn [_ [_ id]]
    {:http {:method  DELETE
            :url (str site-uri "/menus/" id)
            :success-event [:menus/delete-ok id]}}))

(reg-event-db
  :menus/init
  (fn [db _]
    (-> db
      (dissoc :menus :menus/query-params :menus/edit)
      (assoc :menus/add-status false
             :menus/edit-status false
             :menus/delete-status false))))

(reg-event-db
  :menus/select
  (fn [db [_ menu]]
    (assoc db :menus/selected menu)))
