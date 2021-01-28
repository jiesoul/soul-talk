(ns soul-talk.role.events
  (:require [re-frame.core :as rf :refer [reg-event-fx reg-event-db]]
            [soul-talk.db :refer [site-uri]]
            [ajax.core :refer [GET POST PATCH DELETE]]
            [soul-talk.utils :as utils]))

(rf/reg-event-db
  :roles/init
  (fn [db _]
    (-> db
      (assoc :roles/delete-dialog-open false)
      (dissoc
        :roles/query-params
        :roles/pagination
        :roles))))

(rf/reg-event-db
  :roles/set-delete-dialog-open
  (fn [db [_ value]]
    (assoc db :roles/delete-dialog-open value)))

(rf/reg-event-db
  :roles/set-query-params
  (fn [db [_ key value]]
    (assoc-in db [:roles/query-params key] value)))

(rf/reg-event-db
  :roles/clean-query-params
  (fn [db _]
    (dissoc db :roles/query-params)))

(rf/reg-event-db
  :roles/load-page-ok
  (fn [db [_ {:keys [roles pagination params]}]]
    (assoc db :roles roles :roles/pagination pagination :roles/query-params params)))

(rf/reg-event-fx
  :roles/load-page
  (fn [_ [_ params]]
    {:http {:method GET
            :url (str site-uri "/roles")
            :ajax-map {:params params}
            :success-event [:roles/load-page-ok]}}))

(reg-event-db
  :roles/clean-role
  (fn [db _]
    (dissoc db :role)))

(reg-event-db
  :roles/set-attr
  (fn [db [_ attr]]
    (let [edit (:roles/edit db)]
      (assoc db :roles/edit (merge edit attr)))))

(reg-event-db
  :roles/checked-menu
  (fn [db [_ {:keys [id pid]} checked]]
    (let [menus (:menus db)
          child-ids (set (map :id (filter #(= id (:pid %)) menus)))
          role (:roles/edit db)
          ids (set (:menus-ids role))]
      (if checked
        (let [menus-ids (conj ids id)
              menus-ids (if-not (zero? pid) (conj menus-ids pid) menus-ids)
              menus-ids (reduce conj menus-ids child-ids)]
          (assoc-in db [:roles/edit :menus-ids] menus-ids))
        (let [menus-ids (remove #(contains? (conj child-ids id) %) ids)
              brother-ids (set (map :id (filter #(and (= pid (:pid %)) (not= id (:id %))) menus)))
              brother-ids (filter #(contains? brother-ids %) menus-ids)
              menus-ids (if (empty? brother-ids) (remove #(= pid %) menus-ids) menus-ids)]
          (js/console.log "----" brother-ids)
          (js/console.log "----" menus-ids)
          (assoc-in db [:roles/edit :menus-ids] menus-ids))))))

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
  :roles/load-role-ok
  (fn [db [_ {:keys [role]}]]
    (assoc db :roles/edit role)))

(reg-event-fx
  :roles/load-role
  (fn [_ [_ id]]
    {:http {:method GET
            :url (str site-uri "/roles/" id)
            :success-event [:roles/load-role-ok]}}))

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
  :roles/load-role-menus-ok
  (fn [db [_ {:keys [role-menus]}]]
    (assoc db :roles/role-menus role-menus)))

(reg-event-fx
  :roles/load-role-menus
  (fn [_ [_ id]]
    {:http {:method GET
            :url (str site-uri "/roles/" id "/menus")
            :success-event [:roles/load-role-menus-ok]}}))

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

