(ns soul-talk.category.views
  (:require [soul-talk.common.views :as c]
            [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [soul-talk.utils :as du]
            [soul-talk.routes :refer [navigate!]]
            ["semantic-ui-react" :refer [Form Button Table Divider Container]]))

(def ^:dynamic *visible* (r/atom false))

(defn new []
  (let [user (subscribe [:user])
        user-id (:id @user)
        series (subscribe [:category/edit])
        _ (dispatch [:category/set-attr {:create_by user-id :update_by user-id}])]
    [c/layout
     [c/form-layout
      [:> Form
       [:> Form.Input {:title     "name"
                       :label     "名称"
                       :required  true
                       :on-change #(dispatch [:category/set-attr {:name (du/event-value %)}])}]
       [:> Form.Input {:label     "简介"
                       :required  true
                       :on-change #(dispatch [:category/set-attr {:description (du/event-value %)}])}]
       [:div.button-center
        [:> Button {:content  "返回"
                    :on-click #(navigate! (str "/categories"))}]
        [:> Button {:content  "保存"
                    :positive true
                    :on-click #(dispatch [:category/save @series])}]]]]]))

(defn edit []
  (let [user (subscribe [:user])
        series (subscribe [:category/edit])
        _ (dispatch [:category/set-attr {:update_by (:id @user)}])]
    [c/layout
     [c/form-layout
      [:> Form
       [:> Form.Input {:title         "name"
                       :label         "name"
                       :required      true
                       :default-value (:name @series)
                       :on-change     #(dispatch [:category/set-attr {:name (du/event-value %)}])}]
       [:> Form.Input {:label         "简介"
                       :required      true
                       :default-value (:description @series)
                       :on-change     #(dispatch [:category/set-attr {:description (du/event-value %)}])}]
       [:div.button-center
        [:> Button {:content  "返回"
                    :on-click #(navigate! (str "/categories"))}]
        [:> Button {:content  "保存"
                    :positive true
                    :on-click #(dispatch [:category/update @series])}]]]]]))

(defn- delete-modal []
  (let [category (subscribe [:category/edit])
        delete-status (subscribe [:category/delete-dialog])]
    (if @category
      (let [{:keys [id name]} @category]
        ^{:key "delete-category-dialog"}
        [c/confirm {:open   @delete-status
                  :title    "删除分类"
                  :ok-text  "确认"
                  :on-close #(dispatch [:category/set-delete-dialog false])
                  :on-ok    #(do
                               (dispatch [:category/set-delete-dialog false])
                               (dispatch [:category/delete id]))}
         (str "你确定要删除系列 " name " 吗？")]))))

(defn query-form []
  (let [params (subscribe [:category/query-params])]
    [:> Form
     [:> Form.Group
      [:> Form.Input {:label       "名称"
                      :inline      true
                      :on-change   #(dispatch [:category/set-query-params {:name (du/event-value %)}])}]]
     [:div.button-center
      [:> Button {:basic    true
                  :icon "search"
                  :content  "查询"
                  :on-click #(dispatch [:category/load-page @params])}]
      [:> Button {:color "green"
                  :icon "add"
                  :content  "新增"
                  :on-click #(navigate! "/category/new")}]]]))

(defn list-table []
  (let [series-list (subscribe [:category/list])
        query-params (subscribe [:category/query-params])
        pagination (subscribe [:category/pagination])]
    [:div
     [:> Table {:celled     true
                :text-align "center"}
      [:> Table.Header
       [:> Table.Row
        [:> Table.HeaderCell "序号"]
        [:> Table.HeaderCell "名称"]
        [:> Table.HeaderCell "简介"]
        [:> Table.HeaderCell "创建时间"]
        [:> Table.HeaderCell "更新时间"]
        [:> Table.HeaderCell "操作"]]]
      [:> Table.Body
       (doall
         (for [{:keys [id name description create_at update_at] :as series} @series-list]
           ^{:key series}
           [:> Table.Row
            [:> Table.Cell 1]
            [:> Table.Cell name]
            [:> Table.Cell description]
            [:> Table.Cell (du/to-date-time create_at)]
            [:> Table.Cell (du/to-date-time update_at)]
            [:> Table.Cell
             [:div
              [:> Button {:color "green"
                          :alt      "修改"
                          :icon     "edit"
                          :on-click #(navigate! (str "/category/" id "/edit"))}]
              [:> Button {:color    "red"
                          :alt      "删除"
                          :icon     "delete"
                          :on-click (fn []
                                      (dispatch [:category/set-attr {:id id :name name}])
                                      (dispatch [:category/set-delete-dialog true]))}]]]]))]]
     (if @series-list
       [c/table-page :category/load-page (merge @query-params @pagination)])]))

(defn home []
  [c/layout
   [:<>
    [query-form]
    [:> Divider]
    [delete-modal]
    [list-table]]])