(ns soul-talk.pages.post
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [soul-talk.pages.common :as c]
            [taoensso.timbre :as log])
  (:import [goog.History]))

(defn posts-list []
  (r/with-let [posts (subscribe [:admin/posts])]
    [:table.table.table-striped
     [:thead
      [:tr
       [:th "#"]
       [:th "title"]
       [:th "create_time"]
       [:th "modify_time"]
       [:th "publish"]
       [:th "author"]
       [:th "counter"]]]
     [:tbody
      (for [{:keys [title create_time modify_time publish author counter] :as post} @posts]
        ^{:key post}
        [:tr
         [:td title]
         [:td create_time]
         [:td modify_time]
         [:td publish]
         [:td author]
         [:td counter]
         [:td counter]])]]))

(defn posts-page []
  (r/with-let [user (subscribe [:user])]
    [:div.container-fluid
     [:h3 "Post Action"]
     [:hr]
     [:a.btn.btn-primary {:href "/posts/add"}
      "Create"]
     [:h3 "Post List"]
     [:hr]
     [posts-list]]))


(defn create-post-page []
  (r/with-let [user (subscribe [:user])
               categories (subscribe [:categories])
               tags (subscribe [:tags])
               error (subscribe [:error])
               post (r/atom {:author (:name @user)
                             :publish 0})]
    [:div.container-fluid
     [:h3 "Post Add"]
     [:hr]
     [:div.container
      [:div
       [:div.form-group
        [c/text-input "标题" :title "请输入标题" post]]
       [:div.form-group
        [:label {:for "category"} "分类"]
        [:select#category.form-control
         {:on-change #(swap! post assoc :category (-> % .-target .-value))}
         [:option ""]
         (for [{:keys [id name]} @categories]
           ^{:key id}
           [:option {:value id} name])]]
       [:div.form-group
        [:label {:for "content"} "正文"]
        [:textarea#content.form-control
         {:on-change #(swap! post assoc :content (-> % .-target .-value))
          :row 10}]]
       (when @error
         [:div.alert.alert-danger @error])
       [:div.form-group
        [:a.btn.btn-primary
         {:on-click #(dispatch [:posts/add @post])}
         "save"]]]]]))