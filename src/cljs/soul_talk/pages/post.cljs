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
     [:a.btn.btn-primary {:href "/create-post"}
      "Create"]
     [:h3 "Post List"]
     [:hr]
     [posts-list]]))


(defn create-post-page []
  (r/with-let [user (subscribe [:user])
               categories (subscribe [:categories])
               tags (subscribe [:tags])
               post (r/atom {})]
    [:div.container-fluid
     [:h3 "Post Create"]
     [:hr]
     [:div.form-signin
      [:div.form-group.form-contral
       [:div.form-group
        [:label {:for "title"} ]
        [:input#title
         {:type :text
          :value (:title @post)
          :on-change #(swap! post assoc :title (-> % .-target .-value))}]]
       ;[c/text-input "Title" "title" "please title" post]
       [:div.form-group
        [:label {:for "category"}]
        [:select#category.form-control
         [:option ""]
         (for [{:keys [id name]} @categories]
           ^{:key id}
           [:option {:value id} name])]]
       [:div.form-group
        [:label {:for "content"}]
        [:textarea#content.form-control
         {:row 3}]]]]]))