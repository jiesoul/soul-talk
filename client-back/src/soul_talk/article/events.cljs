(ns soul-talk.article.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db subscribe]]
            [ajax.core :refer [POST GET DELETE PUT]]
            [soul-talk.db :refer [site-uri]]
            [taoensso.timbre :as log]))

(reg-event-db
  :set-articles
  (fn [db [_ {:keys [articles pagination query-str]}]]
    (assoc db :articles articles
              :pagination pagination
              :query-str query-str)))

(reg-event-fx
  :load-articles
  (fn [_ [_ pagination]]
    {:http {:method        GET
            :url           (str site-uri "/articles")
            :ajax-map      {:params pagination}
            :success-event [:set-articles]}}))

(reg-event-db
  :clear-articles
  (fn [db _]
    (dissoc db :all-articles)))

(reg-event-fx
  :articles/add-ok
  (fn [{:keys [db]} [_ {:keys [data]}]]
    {:dispatch-n (list [:dispatch (str "/articles/" (get-in data :article :id) "/edit")])}))

(reg-event-fx
  :articles/add
  (fn [_ [_ article]]
    {:http {:method        POST
            :url           (str site-uri "/articles")
            :ajax-map      {:params article}
            :success-event [:articles/add-ok article]}}))

(reg-event-fx
  :articles/edit-ok
  (fn [_ _]
    {:dispatch-n (list [:set-success "保存成功"]
                        [:admin/load-articles])}))

(reg-event-fx
  :articles/edit
  (fn [_ [_ {:keys [id counter] :as article}]]
    {:http {:method        PUT
            :url           (str site-uri "/articles/" id)
            :ajax-map      {:params article}
            :success-event [:articles/edit-ok]}}))

(reg-event-db
  :set-article
  (fn [db [_ {article :article}]]
    (assoc db :article article)))

(reg-event-fx
  :load-article
  (fn [_ [_ id]]
    {:http {:method        GET
            :url           (str site-uri "/articles/" id)
            :success-event [:set-article]}}))

(reg-event-db
  :clear-article
  (fn [db _]
    (dissoc db :article)))

(reg-event-db
  :articles/delete-ok
  (fn [db [_ id]]
    (let [articles (get db :admin/articles)
          articles (remove #(= id (:id %)) articles)]
      (assoc db :success "删除成功" :admin/articles articles))))

(reg-event-db
  :articles/delete-error
  (fn [_ _]
    (js/alert "delete fail")))

(reg-event-fx
  :articles/delete
  (fn [_ [_ id]]
    {:http {:method        DELETE
            :url           (str site-uri "/articles/" id)
            :success-event [:articles/delete-ok id]
            :error-event   [:articles/delete-error]}}))

(reg-event-fx
  :articles/publish-ok
  (fn [_ _]
    {:dispatch-n (list [:set-success "发布成功"]
                        [:admin/load-articles])}))

(reg-event-fx
  :articles/publish-error
  (fn [_ _]
    (js/alert "publish failed")))

(reg-event-fx
  :articles/publish
  (fn [_ [_ id]]
    {:http {:method PUT
            :url (str site-uri "/articles/" id "/publish")
            :success-event [:articles/publish-ok]
            :error-event [:articles/publish-error]}}))

