(ns soul-talk.tag.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
            [ajax.core :refer [GET POST DELETE PUT PATCH]]
            [clojure.string :as str]
            [soul-talk.db :refer [site-uri]]))

(reg-event-db
  :tag/init
  (fn [db _]
    (-> db
      (assoc :tag/delete-dialog false)
      (dissoc :tag/query-params :tag/pagination :tag/list))))

(reg-event-db
  :tag/set-delete-dialog
  (fn [db [_ value]]
    (assoc db :tag/delete-dialog value)))

(reg-event-db
  :tag/load-page-ok
  (fn [db [_ {:keys [tags pagination]}]]
    (assoc db :tag/list tags :pagination pagination)))

(reg-event-fx
  :tag/load-page
  (fn [_ params]
    {:http {:method        GET
            :url           (str site-uri "/tags")
            :ajax-map      {:params params}
            :success-event [:tag/load-page-ok]}}))

(reg-event-db
  :tag/clean-edit
  (fn [db _]
    (dissoc db :tag/edit)))

(reg-event-db
  :tag/set-attr
  (fn [db [_ attr]]
    (update-in db [:tag/edit] merge attr)))

(reg-event-fx
  :tag/save
  (fn [_ [_ {:keys [name] :as tag}]]
    (if (str/blank? name)
      {:dispatch [:set-error "名称不能为空"]}
      {:http {:method        POST
              :url           (str site-uri "/tags")
              :ajax-map      {:params tag}
              :success-event [:set-success "保存成功"]}})))

(reg-event-db
  :tag/load-ok
  (fn [db [_ {:keys [tag]}]]
    (assoc db :tag/edit tag)))

(reg-event-fx
  :tag/load
  (fn [_ [_ id]]
    {:http {:method GET
            :url (str site-uri "/tags/" id)
            :success-event [:tag/load-ok]}}))

(reg-event-fx
  :tag/update
  (fn [_ [_ {:keys [name id] :as tag}]]
    (if (str/blank? name)
      {:dispatch [:set-error "名称不能为空"]}
      {:http {:method        PATCH
              :url           (str site-uri "/tags/" id)
              :ajax-map      {:params tag}
              :success-event [:set-success "保存成功"]}})))

(reg-event-fx
  :tag/delete-ok
  (fn [{:keys [db]} [_ id]]
    (let [tags (:tag db)
          tags (remove #(= id (:id %)) tags)]
      {:db (assoc db :success "删除成功" :tag tags)
       :dispatch [:set-success "删除成功"]})))

(reg-event-fx
  :tag/delete
  (fn [_ [_ id]]
    {:http {:method  DELETE
            :url (str site-uri "/tags/" id)
            :success-event [:tag/delete-ok id]}}))
