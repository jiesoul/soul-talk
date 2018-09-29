(ns soul-talk.pages.category
  (:require [soul-talk.pages.common :as c]
            [re-frame.core :refer [dispatch subscribe]]
            [reagent.core :as r]))

(defn categories-list []
  (r/with-let [categories (subscribe [:categories])
               user (subscribe [:user])]
    (fn []
      [:table.table.table-striped
       [:thead
        [:tr
         [:th "name"]
         [:th "action"]]]
       [:tbody
        (for [{:keys [id name] :as category} @categories]
          ^{:key id}
          [:tr
           [:td name]
           [:td
            (if @user
              [:a.btn.btn-outline-primary.btn-sm
               {:on-click #(dispatch [:categories/delete category])}
               "删除"])]])]])))

(defn categories-page []
  (r/with-let [user (subscribe [:user])]
    (fn []
      [:div.container-fluid
       [:h4 "分类管理"]
       [:hr]
       [:div.p-1
        [:a.btn.btn-outline-primary.btn-sm
         {:href "/categories/add"}
         "添加"]]
       [categories-list]])))


(defn add-page []
  (r/with-let
    [category (r/atom {})
     error (subscribe [:error])]
    (fn []
      [c/modal
       [:div "分类添加"]
       [:div.form-group
        [c/text-input "Name" :name "please enter name" category]
        (when @error
          [:div.alert.alert-danger.smaller @error])]
       [:div
        [:a.btn.btn-outline-primary.btn-sm
         {:href "/categories"}
         "返回"]
        [:a.btn.btn-outline-primary.btn-sm
         {:value    "Add"
          :on-click #(dispatch [:categories/add @category])}
         "保存"]]])))
