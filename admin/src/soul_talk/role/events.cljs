(ns soul-talk.role.events
  (:require [re-frame.core :as rf :refer [reg-event-fx reg-event-db]]
            [soul-talk.db :refer [api-url]]
            [ajax.core :refer [GET POST PATCH DELETE]]
            [soul-talk.utils :as utils]
            [clojure.string :as str]))

(rf/reg-event-db
  :role/init
  (fn [db _]
    (-> db
      (assoc :role/delete-dialog false)
      (dissoc
        :role/edit
        :role/query-params
        :role/pagination
        :role/list))))

(rf/reg-event-db
  :role/set-delete-dialog
  (fn [db [_ value]]
    (assoc db :role/delete-dialog value)))

(rf/reg-event-db
  :role/set-query-params
  (fn [db [_ key value]]
    (assoc-in db [:role/query-params key] value)))

(rf/reg-event-db
  :role/clean-query-params
  (fn [db _]
    (dissoc db :role/query-params)))

(rf/reg-event-db
  :role/load-page-ok
  (fn [db [_ {:keys [roles pagination params]}]]
    (assoc db :role/list roles :role/pagination pagination :role/query-params params)))

(rf/reg-event-fx
  :role/load-page
  (fn [_ [_ params]]
    {:http {:method GET
            :url (str api-url "/roles")
            :ajax-map {:params params}
            :success-event [:role/load-page-ok]}}))

(reg-event-db
  :role/clean-role
  (fn [db _]
    (dissoc db :role)))

(reg-event-db
  :role/set-attr
  (fn [db [_ attr]]
    (let [edit (:role/edit db)]
      (assoc db :role/edit (merge edit attr)))))

(reg-event-db
  :role/checked-menu
  (fn [db [_ {:keys [id pid]} checked]]
    (let [menus (:menus db)
          child-ids (set (map :id (filter #(= id (:pid %)) menus)))
          role (:role/edit db)
          ids (set (:menus-ids role))]
      (if checked
        (let [menus-ids (conj ids id)
              menus-ids (if-not (zero? pid) (conj menus-ids pid) menus-ids)
              menus-ids (reduce conj menus-ids child-ids)]
          (assoc-in db [:role/edit :menus-ids] menus-ids))
        (let [menus-ids (remove #(contains? (conj child-ids id) %) ids)
              brother-ids (set (map :id (filter #(and (= pid (:pid %)) (not= id (:id %))) menus)))
              brother-ids (filter #(contains? brother-ids %) menus-ids)
              menus-ids (if (empty? brother-ids) (remove #(= pid %) menus-ids) menus-ids)]
          (js/console.log "----" brother-ids)
          (js/console.log "----" menus-ids)
          (assoc-in db [:role/edit :menus-ids] menus-ids))))))

(reg-event-fx
  :role/save
  (fn [_ [_ role]]
    {:http {:method        POST
            :url           (str api-url "/roles")
            :ajax-map      {:params role}
            :success-event [:set-success "保存成功"]}}))

(reg-event-db
  :role/load-role-ok
  (fn [db [_ {:keys [role]}]]
    (assoc db :role/edit role)))

(reg-event-fx
  :role/load-role
  (fn [_ [_ id]]
    {:http {:method GET
            :url (str api-url "/roles/" id)
            :success-event [:role/load-role-ok]}}))

(reg-event-fx
  :role/update
  (fn [_ [_ role]]
    {:http {:method        PATCH
            :url           (str api-url "/roles")
            :ajax-map      {:params role}
            :success-event [:set-success "保存成功"]}}))

(reg-event-fx
  :role/delete-ok
  (fn [{:keys [db]} [_ id]]
    (let [roles (:role db)
          roles (remove #(= id (:id %)) roles)]
      {:db (assoc db :role roles)
       :dispatch [:set-success "删除成功"]})))

(reg-event-fx
  :role/delete
  (fn [_ [_ id]]
    {:http {:method  DELETE
            :url (str api-url "/roles/" id)
            :success-event [:role/delete-ok id]}}))

(reg-event-db
  :role/load-role-menus-ok
  (fn [db [_ {:keys [role-menus]}]]
    (assoc db :role/role-menus role-menus)))

(reg-event-fx
  :role/load-role-menus
  (fn [_ [_ id]]
    {:http {:method GET
            :url (str api-url "/roles/" id "/menus")
            :success-event [:role/load-role-menus-ok]}}))

(rf/reg-event-fx
  :role/load-menus-ok
  (fn [_ [_ {:keys [role-menus]}]]
    (let [menu-ids (str/join "," (map :menu_id role-menus))]
      {:dispatch-n (list [:menu/load-menus menu-ids])})))

(rf/reg-event-fx
  :role/load-menus
  (fn [_ [_ ids]]
    {:http {:method        GET
            :url           (str api-url "/roles/menus")
            :ajax-map      {:params {:ids ids}}
            :success-event [:role/load-menus-ok]}}))

