(ns soul-talk.article.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db subscribe ->interceptor] :as rf]
            [ajax.core :refer [POST GET DELETE PUT PATCH]]
            [soul-talk.db :refer [site-uri]]))

(reg-event-db
  :article/init
  (fn [db _]
    (-> db
      (dissoc :article/list :article/query-params :article/pagination)
      (assoc :article/delete-dialog false
             :article/publish-dialog false))))

(reg-event-db
  :article/set-publish-dialog
  (fn [db [_ value]]
    (assoc db :article/publish-dialog value)))

(reg-event-db
  :article/set-delete-dialog
  (fn [db [_ value]]
    (assoc db :article/delete-dialog value)))

(reg-event-db
  :article/load-page-ok
  (fn [db [_ {:keys [articles pagination query-params]}]]
    (assoc db :article/list articles
              :article/pagination pagination
              :article/query-str query-params)))

(reg-event-fx
  :article/load-page
  (fn [_ [_ pagination]]
    {:http {:method        GET
            :url           (str site-uri "/articles")
            :ajax-map      {:params pagination}
            :success-event [:article/load-page-ok]}}))

(reg-event-db
  :article/clear-edit
  (fn [db _]
    (dissoc db :article/edit)))


(reg-event-db
  :article/set-attr
  (fn [db [_ attr]]
    (let [edit (:article/edit db)]
      (assoc db :article/edit (merge edit attr)))))

(def article-validate
  (->interceptor
    :id "article-validate"
    :before (fn [context]
              (let [{:keys [db event]} (:coeffects context)
                    [_ article] event
                    {:keys [title ]} article]
                (if title
                  (-> context
                    (update :db assoc :error "标题不能为空"))
                  context)))))

(reg-event-fx
  :article/save-ok
  (fn [{:keys [db]} [_ {:keys [article]}]]
    {:db         (update-in db [:article] conj article)
     :dispatch-n [[:set-success "保存成功"]
                  [:article/clear-edit]]}))

(reg-event-fx
  :article/save
  [article-validate]
  (fn [_ [_ {:keys [title] :as article}]]
    {:http {:method        POST
            :url           (str site-uri "/articles")
            :ajax-map      {:params article}
            :success-event [:article/save-ok]}}))

(reg-event-fx
  :article/update
  (fn [_ [_ {:keys [id counter] :as article}]]
    {:http {:method        PATCH
            :url           (str site-uri "/articles/" id)
            :ajax-map      {:params article}
            :success-event [:set-success "更新成功"]}}))

(reg-event-db
  :article/load-ok
  (fn [db [_ {article :article}]]
    (assoc db :article/edit article)))

(reg-event-fx
  :article/load
  (fn [_ [_ id]]
    {:http {:method        GET
            :url           (str site-uri "/articles/" id)
            :success-event [:article/load-ok]}}))

(reg-event-db
  :article/delete-ok
  (fn [db [_ id]]
    (let [article (get db :article)
          article (remove #(= id (:id %)) article)]
      (assoc db :success "删除成功" :article article))))

(reg-event-fx
  :article/delete
  (fn [_ [_ id]]
    {:http {:method        DELETE
            :url           (str site-uri "/articles/" id)
            :success-event [:article/delete-ok id]}}))

(reg-event-db
  :article/publish-ok
  (fn [db [_ {:keys [article]}]]
    (assoc db :success "发布成功" :article/edit article)))

(reg-event-fx
  :article/publish
  (fn [_ [_ id]]
    (js/console.log "doing publish article")
    {:http {:method PATCH
            :url (str site-uri "/articles/" id "/publish")
            :success-event [:article/publish-ok]}}))

