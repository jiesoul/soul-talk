(ns soul-talk.article.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db subscribe ->interceptor] :as rf]
            [ajax.core :refer [POST GET DELETE PUT PATCH]]
            [soul-talk.db :refer [api-url]]))

(reg-event-db
  :articles/init
  (fn [db _]
    (-> db
      (dissoc :articles :articles/query-params :articles/pagination)
      (assoc :articles/delete-dialog-open false
             :articles/publish-dialog false))))

(reg-event-db
  :articles/set-publish-dialog
  (fn [db [_ value]]
    (assoc db :articles/publish-dialog value)))

(reg-event-db
  :articles/set-delete-dialog-open
  (fn [db [_ value]]
    (assoc db :articles/delete-dialog-open value)))

(reg-event-db
  :articles/load-page-ok
  (fn [db [_ {:keys [articles pagination query-params]}]]
    (assoc db :articles articles
              :articles/pagination pagination
              :articles/query-str query-params)))

(reg-event-fx
  :articles/load-page
  (fn [_ [_ pagination]]
    {:http {:method        GET
            :url           (str api-url "/articles")
            :ajax-map      {:params pagination}
            :success-event [:articles/load-page-ok]}}))

(reg-event-db
  :articles/clear-edit
  (fn [db _]
    (dissoc db :articles/edit)))


(reg-event-db
  :articles/set-attr
  (fn [db [_ attr]]
    (let [edit (:articles/edit db)]
      (assoc db :articles/edit (merge edit attr)))))

(def articles-validate
  (->interceptor
    :id "articles-validate"
    :before (fn [context]
              (let [{:keys [db event]} (:coeffects context)
                    [_ article] event
                    {:keys [title ]} article]
                (if title
                  (-> context
                    (update :db assoc :error "标题不能为空"))
                  context)))))

(reg-event-db
  :articles/new-ok
  (fn [db [_ {:keys [article]}]]
    (let [articles (:articles db)]
      (assoc db :success "保存成功" :articles (conj articles article)))))

(reg-event-fx
  :articles/new
  [articles-validate]
  (fn [_ [_ {:keys [title] :as article}]]
    {:http {:method        POST
            :url           (str api-url "/articles")
            :ajax-map      {:params article}
            :success-event [:articles/new-ok article]}}))

(reg-event-db
  :articles/update-ok
  (fn [db _]
    (assoc db :success "保存成功")))

(reg-event-fx
  :articles/update
  (fn [_ [_ {:keys [id counter] :as article}]]
    {:http {:method        PATCH
            :url           (str api-url "/articles/" id)
            :ajax-map      {:params article}
            :success-event [:articles/update-ok]}}))

(reg-event-db
  :articles/load-article-ok
  (fn [db [_ {article :article}]]
    (assoc db :articles/edit article)))

(reg-event-fx
  :articles/load-article
  (fn [_ [_ id]]
    {:http {:method        GET
            :url           (str api-url "/articles/" id)
            :success-event [:articles/load-article-ok]}}))

(reg-event-db
  :articles/delete-ok
  (fn [db [_ id]]
    (let [articles (get db :articles)
          articles (remove #(= id (:id %)) articles)]
      (assoc db :success "删除成功" :articles articles))))

(reg-event-fx
  :articles/delete
  (fn [_ [_ id]]
    {:http {:method        DELETE
            :url           (str api-url "/articles/" id)
            :success-event [:articles/delete-ok id]}}))

(reg-event-db
  :articles/publish-ok
  (fn [db [_ {:keys [article]}]]
    (assoc db :success "发布成功" :articles/edit article)))

(reg-event-fx
  :articles/publish
  (fn [_ [_ id]]
    (js/console.log "doing publish articles")
    {:http {:method PATCH
            :url (str api-url "/articles/" id "/publish")
            :success-event [:articles/publish-ok]}}))

