(ns soul-talk.user.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db subscribe reg-fx]]
            [ajax.core :refer [GET POST PUT PATCH DELETE]]
            [soul-talk.db :refer [site-uri]]
            [soul-talk.common.local-storage :as storage]
            [clojure.string :as str]))

(reg-fx
  :set-user!
  (fn [user-identity]
    (storage/set-item! storage/login-user-key user-identity)))

(reg-fx
  :clean-user!
  (fn []
    (storage/remove-item! storage/login-user-key)))

(reg-event-db
  :users/init
  (fn [db _]
    (assoc db :users/new-dialog-open false
              :users/edit-dialog-open false
              :users/delete-dialog-open false
              :users/roles-dialog-open false
              :users/query-params nil
              :users/list nil
              :users/pagination nil)))

(reg-event-db
  :users/set-add-dialog-open
  (fn [db [_ value]]
    (assoc db :users/new-dialog-open value)))

(reg-event-db
  :users/set-edit-dialog-open
  (fn [db [_ value]]
    (assoc db :users/edit-dialog-open value)))

(reg-event-db
  :users/set-delete-dialog-open
  (fn [db [_ value]]
    (assoc db :users/delete-dialog-open value)))

(reg-event-db
  :users/set-roles-dialog-open
  (fn [db [_ value]]
    (assoc db :users/roles-dialog-open value)))

(reg-event-fx
  :users/load-roles-ok
  (fn [{:keys [db]} [_ {:keys [user-roles]}]]
    (let [role-ids (map :role_id user-roles)]
      {:db         (assoc-in db [:user :user-roles] user-roles)
       :dispatch-n (list [:roles/load-menus role-ids])})))

(reg-event-fx
  :users/load-roles
  (fn [_ [_ id]]
    {:http {:method        GET
            :url           (str site-uri "/users/" id "/roles")
            :success-event [:users/load-roles-ok]}}))

(reg-event-db
  :users/load-all-ok
  (fn [db [_ {:keys [users]}]]
    (assoc db :users users)))

(reg-event-fx
  :users/load-all
  (fn [_ _]
    {:http {:method        GET
            :url           (str site-uri "/users")
            :success-event [:users/load-all-ok]}}))

(reg-event-db
  :users/load-user-ok
  (fn [db [_ {:keys [user]}]]
    (assoc db :users/user user)))

(reg-event-fx
  :users/load-user
  (fn [_ [_ id]]
    {:http {:method GET
            :url (str site-uri "/users/" id)
            :success-event [:users/load-user-ok]}}))

(reg-event-fx
  :users/change-password
  [reagent.debug/tracking]
  (fn [_ [_ {:keys [id old-password new-password confirm-password] :as params}]]
    (println "params: " params)
    (if (str/blank? old-password)
      {:dispatch [:set-error "旧密码不能为空"]}
      (if (str/blank? new-password)
        {:dispatch [:set-error "新密码不能为空"]}
        (if (str/blank? confirm-password)
          {:dispatch [:set-error "确认密码不能为空"]}
          (if (not= new-password confirm-password)
            {:dispatch [:set-error "新密码和确认密码必须一样"]}
            {:http {:method        PATCH
                    :url           (str site-uri "/users/" id "/password")
                    :ajax-map      {:params params}
                    :success-event [:set-success "修改密码成功"]}}))))))

(reg-event-fx
  :users/user-profile
  (fn [_ [_ {:keys [id email name] :as user}]]
    {:http {:method        PATCH
            :url           (str site-uri "/users/" id "")
            :ajax-map      {:params user}
            :success-event [:set-success "保存信息成功"]}}))

(reg-event-db
  :users/set-query-params
  (fn [db [_ key value]]
    (assoc-in db [:users/query-params key] value)))

(reg-event-db
  :users/load-page-ok
  (fn [db [_ {:keys [users pagination query-params]}]]
    (assoc db :users/list users
              :users/pagination pagination
              :users/query-params query-params)))

(reg-event-fx
  :users/load-page
  (fn [_ [_ params]]
    {:http {:method GET
            :url (str site-uri "/users")
            :ajax-map {:params params}
            :success-event [:users/load-page-ok]}}))