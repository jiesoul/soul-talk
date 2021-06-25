(ns soul-talk.category.events
  (:require [re-frame.core :refer [reg-event-db reg-event-fx]]
            [ajax.core :refer [GET POST PATCH PUT DELETE]]
            [soul-talk.db :refer [api-url]]
            [clojure.string :as str]))

(def category-url (str api-url "/categories"))

(reg-event-db
  :category/init
  (fn [db _]
    (-> db
      (assoc :category/delete-dialog false)
      (dissoc :category/list :category/pagination :category/query-params :category/edit))))

(reg-event-db
  :category/set-delete-dialog
  (fn [db [_ value]]
    (assoc db :category/delete-dialog value)))

(reg-event-db
  :category/set-attr
  (fn [db [_ attr]]
    (update-in db [:category/edit] merge attr)))

(reg-event-db
  :category/load-all-ok
  (fn [db [_ {:keys [series pagination]}]]
    (assoc db :category/list series :category/pagination pagination)))

(reg-event-fx
  :category/load-all
  (fn [_ params]
    {:http {:method        GET
            :url           category-url
            :ajax-map      {:params params}
            :success-event [:category/load-all-ok]}}))

(reg-event-db
  :category/set-query-params
  (fn [db [_ m]]
    (update-in db [:category/query-params] merge m)))

(reg-event-db
  :category/load-page-ok
  (fn [db [_ {:keys [categories query-params pagination] :as r}]]
    (assoc db :category/list categories
              :category/query-params query-params
              :category/pagination pagination)))

(reg-event-fx
  :category/load-page
  (fn [_ [_ params]]
    {:http {:method GET
            :url category-url
            :ajax-map {:params params}
            :success-event [:category/load-page-ok]}}))

(reg-event-fx
  :category/clean-edit
  (fn [db _]
    (dissoc db :category/edit)))

(reg-event-fx
  :category/save
  (fn [_ [_ {:keys [name] :as series}]]
    (if (str/blank? name)
      {:dispatch [:set-error "名称不能为空"]}
      {:http {:method        POST
              :url           category-url
              :ajax-map      {:params series}
              :success-event [:set-success "保存成功"]}})))

(reg-event-db
  :category/load-ok
  (fn [db [_ {:keys [series]}]]
    (assoc db :category/edit series)))

(reg-event-fx
  :category/load
  (fn [_ [_ id]]
    {:http {:method GET
            :url (str category-url "/" id)
            :success-event [:category/load-ok]}}))

(reg-event-fx
  :category/update
  (fn [_ [_ {:keys [name] :as series}]]
    (if (str/blank? name)
      {:dispatch [:set-error "名称不能为空"]}
      {:http {:method        PATCH
              :url           category-url
              :ajax-map      {:params series}
              :success-event [:set-success "保存成功"]}})))

(reg-event-fx
  :category/delete-ok
  (fn [{:keys [db]} [_ id]]
    (let [series-list (:category/list db)
          series-list (remove #(= id (:id %)) series-list)]
      {:db (assoc db :category/list series-list)
       :dispatch [:set-success "删除成功"]})))


(reg-event-fx
  :category/delete
  (fn [_ [_ id]]
    {:http {:method  DELETE
            :url (str category-url "/" id)
            :success-event [:category/delete-ok id]}}))

