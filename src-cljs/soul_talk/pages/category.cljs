(ns soul-talk.pages.category
  (:require [soul-talk.pages.common :as c]
            [re-com.core :refer [input-text]]
            [re-frame.core :refer [dispatch subscribe]]
            [reagent.core :as r]))

(defn categories-list []
  (r/with-let [categories (subscribe [:categories])
               user (subscribe [:user])
               confirm-open? (r/atom false)]
    (when @user
      [:table.table.table-striped
       [:thead
        [:tr
         [:th "name"]
         [:th "action"]]]
       [:tbody
        (doall
          (for [{:keys [id name] :as category} @categories]
            ^{:key id}
            [:tr
             [:td name]
             [:td
              [:a.btn.btn-outline-primary.btn-sm.mr-2
               {:href (str "/categories/" id "/edit")}
               "修改"]
              [:a.btn.btn-outline-primary.btn-sm
               {:on-click #(reset! confirm-open? true)}
               "Delete"
               [c/confirm-modal
                "Are you sure wish to delete the category?"
                confirm-open?
                #(dispatch [:categories/delete category])
                "Delete"]]]]))]])))

(defn categories-page []
  (r/with-let [user (subscribe [:user])]
    (fn []
      (when @user
        [:div.container-fluid
         [:nav {:aria-label "breadcrumb"}
          [:ol.breadcrumb
           [:li.breadcrumb-item.active {:aria-current "page"} "分类管理"]]]
         [:div.p-1
          [:a.btn.btn-outline-primary.btn-sm
           {:href "/categories/add"}
           "添加"]]
         [categories-list]]))))


(defn add-page []
  (r/with-let [ori-category (subscribe [:category])
               category (-> @ori-category
                          (update :name #(or % ""))
                          r/atom)
               error (subscribe [:error])
               name (r/cursor category [:name])]
    (js/console.log @category)
    (js/console.log @ori-category)
    [:div.container-fluid
     [:nav {:aria-label "breadcrumb"}
      [:ol.breadcrumb
       [:li.breadcrumb-item.active
        {:aria-current "page"}
        (if @ori-category "分类修改" "分类添加")]]]
     [:div.form-group
      [input-text
       :model name
       :width "100%"
       :class "form-control"
       :on-change #(reset! name %)]
      (when @error
        [:div.alert.alert-danger.smaller @error])]
     [:div
      [:a.btn.btn-outline-primary.btn-sm
       {:href "/categories"}
       "返回"]
      [:a.btn.btn-outline-primary.btn-sm
       {:on-click #(if @ori-category
                     (dispatch [:categories/edit @category])
                     (dispatch [:categories/add @category]))}
       "保存"]]]))

(defn edit-page []
  (r/with-let [ori-category (subscribe [:category])
               error    (subscribe [:error])]
    (fn []
      (let [category (-> @ori-category
                       (update :name #(or % ""))
                       r/atom)
            name     (r/cursor category [:name])]
        [:div.container-fluid
         [:nav {:aria-label "breadcrumb"}
          [:ol.breadcrumb
           [:li.breadcrumb-item.active
            {:aria-current "page"}
            (if @ori-category "分类修改" "分类添加")]]]
         [:div.form-group
          [input-text
           :model name
           :width "100%"
           :class "form-control"
           :on-change #(reset! name %)]
          (when @error
            [:div.alert.alert-danger.smaller @error])]
         [:div
          [:a.btn.btn-outline-primary.btn-sm
           {:href "/categories"}
           "返回"]
          [:a.btn.btn-outline-primary.btn-sm
           {:on-click #(if @ori-category
                         (dispatch [:categories/edit @category])
                         (dispatch [:categories/add @category]))}
           "保存"]]]))))

