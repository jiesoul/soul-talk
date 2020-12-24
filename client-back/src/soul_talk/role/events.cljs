(ns soul-talk.role.events
  (:require [re-frame.core :as rf :refer [reg-event-fx reg-event-db]]
            [soul-talk.db :refer [site-uri]]
            [ajax.core :refer [GET POST PATCH DELETE]]
            [soul-talk.utils :as utils]))

(rf/reg-event-fx
  :roles/load-menus-ok
  (fn [{:keys [db]} [_ {:keys [role-roles]}]]
    (let [menu-ids (map :menu_id role-roles)]
      {:db         (assoc-in db [:user :role-roles] role-roles)
       :dispatch-n (list [:roles/load-roles menu-ids])})))

(rf/reg-event-fx
  :roles/load-menus
  (fn [_ [_ ids]]
    {:http {:method        GET
            :url           (str site-uri "/roles/menus")
            :ajax-map      {:params {:ids ids}}
            :success-event [:roles/load-menus-ok]}}))

(rf/reg-event-db
  :roles/set-query-params
  (fn [db [_ key value]]
    (assoc-in db [:roles/query-params key] value)))

(rf/reg-event-db
  :roles/clean-query-params
  (fn [db _]
    (js/console.log "clean query params")
    (dissoc db :roles/query-params)))

(rf/reg-event-db
  :roles/load-page-ok
  (fn [db [_ {:keys [roles pagination params]}]]
    (assoc db :roles roles :pagination pagination :roles/query-params params)))

(rf/reg-event-fx
  :roles/load-page
  (fn [_ [_ params]]
    {:http {:method GET
            :url (str site-uri "/roles")
            :ajax-map {:params params}
            :success-event [:roles/load-page-ok]}}))

(reg-event-db
  :roles/load-role-ok
  (fn [db [_ {:keys [role]}]]
    (assoc db :role role)))

(reg-event-fx
  :roles/load-role
  (fn [_ [_ id]]
    (println "**********id: " id)
    {:http {:method GET
            :url (str site-uri "/roles/" id)
            :success-event [:roles/load-role-ok]}}))

(reg-event-db
  :roles/load-role-menus-ok
  (fn [db [_ {:keys [role-menus]}]]
    (assoc db :roles/role-menus role-menus)))

(reg-event-fx
  :roles/load-role-menus
  (fn [_ [_ id]]
    {:http {:method GET
            :url (str site-uri "/roles/" id "/menus")
            :success-event [:roles/load-role-menus-ok]}}))

(reg-event-db
  :roles/clean-role
  (fn [db _]
    (dissoc db :role)))

(reg-event-db
  :roles/set-attr
  (fn [db [_ key value]]
    (assoc-in db [:role key] value)))

(reg-event-db
  :roles/add-ok
  (fn [db [_ {:keys [role]}]]
    (let [roles (:roles db)]
      (assoc db :success "保存成功" :roles (conj roles role)))))

(reg-event-fx
  :roles/add
  (fn [_ [_ role]]
    {:http {:method        POST
            :url           (str site-uri "/roles")
            :ajax-map      {:params role}
            :success-event [:roles/add-ok]}}))

(reg-event-db
  :roles/update-ok
  (fn [db [_ {:keys [role]}]]
    (assoc db :success "保存成功")))

(reg-event-fx
  :roles/update
  (fn [_ [_ role]]
    {:http {:method        PATCH
            :url           (str site-uri "/roles")
            :ajax-map      {:params role}
            :success-event [:roles/update-ok]}}))

(reg-event-db
  :roles/delete-ok
  (fn [db [_ id]]
    (let [roles (:roles db)
          roles (remove #(= id (:id %)) roles)]
      (assoc db :success "删除成功" :roles roles))))

(reg-event-fx
  :roles/delete
  (fn [_ [_ id]]
    {:http {:method  DELETE
            :url (str site-uri "/roles/" id)
            :success-event [:roles/delete-ok id]}}))

(reg-event-db
  :roles/clean
  (fn [db _]
    (dissoc db :roles :roles/query-params :role)))

(reg-event-db
  :roles/select
  (fn [db [_ role]]
    (assoc db :roles/selected role)))