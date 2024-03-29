(ns soul-talk.user.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db reg-fx]]
            [ajax.core :refer [GET POST PUT PATCH DELETE]]
            [soul-talk.db :refer [api-url]]
            [soul-talk.common.local-storage :as storage]
            [clojure.string :as str]))



(reg-event-db
  :user/init
  (fn [db _]
    (assoc db :user/delete-dialog false
              :user/query-params nil
              :user/list nil
              :user/pagination nil)))

(reg-event-db
  :user/set-delete-dialog
  (fn [db [_ value]]
    (assoc db :user/delete-dialog value)))

(reg-event-fx
  :user/load-roles-ok
  (fn [{:keys [db]} [_ {:keys [user-roles]}]]
    (let [role-ids (map :role_id user-roles)]
      {:db         (assoc-in db [:user :user-roles] user-roles)
       :dispatch-n (list [:role/load-menus role-ids])})))

(reg-event-fx
  :user/load-roles
  (fn [_ [_ id]]
    {:http {:method        GET
            :url           (str api-url "/users/" id "/roles")
            :success-event [:user/load-roles-ok]}}))

(reg-event-fx
  :user/load-menus
  (fn [_ [_ id]]
    {:dispatch-n [[:user/load-roles id]]}))

(reg-event-db
  :user/load-all-ok
  (fn [db [_ {:keys [users]}]]
    (assoc db :user users)))

(reg-event-fx
  :user/load-all
  (fn [_ _]
    {:http {:method        GET
            :url           (str api-url "/users")
            :success-event [:user/load-all-ok]}}))

(reg-event-db
  :user/load-user-ok
  (fn [db [_ {:keys [user]}]]
    (assoc db :user/user user)))

(reg-event-fx
  :user/load-user
  (fn [_ [_ id]]
    {:http {:method GET
            :url (str api-url "/users/" id)
            :success-event [:user/load-user-ok]}}))

(reg-event-fx
 :user/change-password
 (fn [_ [_ {:keys [id old-password new-password confirm-password] :as params}]]
   (if (str/blank? old-password)
     {:dispatch [:set-error "旧密码不能为空"]}
     (if (str/blank? new-password)
       {:dispatch [:set-error "新密码不能为空"]}
       (if (str/blank? confirm-password)
         {:dispatch [:set-error "确认密码不能为空"]}
         (if (not= new-password confirm-password)
           {:dispatch [:set-error "新密码和确认密码必须一样"]}
           {:http {:method        PATCH
                   :url           (str api-url "/users/" id "/password")
                   :ajax-map      {:params params}
                   :success-event [:set-success "修改密码成功"]}}))))))

(reg-event-db
  :user/clean-edit
  (fn [db _]
    (dissoc db :user/edit)))

(reg-event-db
  :user/set-attr
  (fn [db [_ user]]
    (update-in db [:user/edit] merge user)))

(reg-event-fx
  :user/user-profile
  (fn [_ [_ {:keys [id] :as user}]]
    {:http {:method        PATCH
            :url           (str api-url "/users/" id "")
            :ajax-map      {:params user}
            :success-event [:set-success "保存信息成功"]}}))

(reg-event-db
  :user/set-query-params
  (fn [db [_ param]]
    (update-in db [:user/query-params] merge param)))

(reg-event-db
  :user/load-page-ok
  (fn [db [_ {:keys [users pagination query-params]}]]
    (assoc db :user/list users
              :user/pagination pagination
              :user/query-params query-params)))

(reg-event-fx
  :user/load-page
  (fn [_ [_ params]]
    {:http {:method GET
            :url (str api-url "/users")
            :ajax-map {:params params}
            :success-event [:user/load-page-ok]}}))

(reg-event-db
  :user/auth-key-init
  (fn [db _]
    (-> db
      (dissoc :user/auth-key-list :user/auth-key-query-params :user/auth-key-pagination))))

(reg-event-db
 :user/set-auth-key-query-params
 (fn [db [_ param]]
   (update-in db [:user/auth-key-query-params] merge param)))

(reg-event-db
  :user/load-auth-key-page-ok
  (fn [db [_ {:keys [auth-keys pagination query-params]}]]
    (assoc db :user/auth-key-list auth-keys :user/auth-key-pagination pagination :user/auth-key-query-params query-params)))

(reg-event-fx
  :user/load-auth-key-page
  (fn [_ [_ params]]
    {:http {:method        GET
            :url           (str api-url "/users/auth-keys")
            :ajax-map      {:params params}
            :success-event [:user/load-auth-key-page-ok]}}))