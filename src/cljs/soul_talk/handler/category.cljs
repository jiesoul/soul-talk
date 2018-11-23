(ns soul-talk.handler.category
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
            [ajax.core :refer [POST GET DELETE PUT]]
            [clojure.string :as str]))

(reg-event-db
  :set-categories
  (fn [db [_ {:keys [categories]}]]
    (assoc db :categories categories)))


(reg-event-fx
  :load-categories
  (fn [_ _]
    {:http {:method GET
            :url "/api/categories"
            :success-event [:set-categories]}}))

(reg-event-fx
  :categories/add-ok
  (fn [_ [_ {:keys [category]}]]
    (js/alert "Add successful")
    {:reload-page true}))

(reg-event-fx
  :categories/add
  (fn [_ [_ {:keys [name] :as category}]]
    (if (str/blank? name)
      {:dispatch [:set-error "名称不能为空"]}
      {:http {:method        POST
              :url           "/api/admin/categories/"
              :ajax-map      {:params category}
              :success-event [:categories/add-ok]
              :error-event #(js/alert "发生错误请重试")}})))

(reg-event-db
  :set-category
  (fn [db [_ {:keys [category]}]]
    (js/console.log category)
    (assoc db :category category)))

(reg-event-db
  :load-category
  (fn [_ [_ id]]
    {:http {:method (str "/api/categories/" id)
            :success-event [:set-category]}}))

(reg-event-db
  :categories/edit-ok
  (fn [db [_ {:keys [category]}]]
    (let [categories (:categories db)]
      (js/alert "update successful"))))

(reg-event-fx
  :categories/edit
  (fn [_ [_ {:keys [name] :as category}]]
    (if (str/blank? name)
      {:dispatch [:set-error "名称不能为空"]}
      {:http {:method PUT
              :url "/api/admin/categories/"
              :ajax-map {:params category}
              :success-event [:categories/update-ok]}})))

(reg-event-fx
  :categories/delete-ok
  (fn [_ _]
    (js/alert "delete successful!")
    {:reload-page true}))

(reg-event-fx
  :categories/delete-error
  (fn [_ [_ {:keys [response]}]]
    (js/alert (:message response))))

(reg-event-fx
  :categories/delete
  (fn [_ [_ {:keys [id name]}]]
    (if (js/confirm (str "你确定要删除分类 " name " 吗？"))
      {:http {:method        DELETE
              :url           (str "/api/admin/categories/" id)
              :success-event [:categories/delete-ok]
              :error-event   [:categories/delete-error]}})))
