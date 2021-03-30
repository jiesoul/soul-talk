(ns soul-talk.tag.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [dispatch subscribe]]
            [soul-talk.common.views :as c]
            [soul-talk.utils :as du]
            [soul-talk.routes :refer [navigate!]]
            ["semantic-ui-react" :refer [Form Button Table Divider Icon Container Card Input]]))

(def ^:dynamic *visible* (r/atom false))

(defn new []
  [c/layout
   (let [user @(subscribe [:user])
         tag (subscribe [:tag/edit])
         _ (dispatch [:tag/set-attr {:create_by (:id user) :update_by (:id user)}])]
     [:> Form
      [:> Form.Input {:required true
                      :label    "名称"
                      :on-change #(dispatch [:tag/set-attr {:name (du/event-value %)}])}]
      [:div.button-center
       [:> Button {:content "返回"
                   :on-click #(navigate! "/tags")}]
       [:> Button {:color "green"
                   :icon "save"
                   :content "保存"
                   :on-click #(dispatch [:tag/save @tag])}]]])])

(defn edit []
  [c/layout
   (let [user (subscribe [:user])
         tag  (subscribe [:tag/edit])]
     [:> Form {:name "add_tag_form"}
      [:> Form.Input {:title         "name"
                      :label         "name"
                      :required      true
                      :default-value (:name @tag)
                      :on-change     #(let [value (-> % .-target .-value)]
                                        (dispatch [:tag/set-attr {:name value}]))}]
      [:div.button-center
       [:> Button {:content "返回"
                   :on-click #(navigate! "/tags")}]
       [:> Button {:color "green"
                   :icon "save"
                   :content "保存"
                   :on-click #(dispatch [:tag/update @tag])}]]])])

(defn- delete-modal []
  (let [tags (subscribe [:tag/edit])
        delete-status (subscribe [:tag/delete-dialog])]
    (if @tags
      (let [{:keys [id name]} @tags]
        ^{:key "delete-tags-dialog"}
        [c/confirm {:open   @delete-status
                  :title    "删除菜单"
                  :ok-text  "确认"
                  :on-close #(dispatch [:tag/set-delete-dialog false])
                  :on-ok    #(do
                               (dispatch [:tag/set-delete-dialog false])
                               (dispatch [:tag/delete id]))}
         (str "你确定要删除系列 " name " 吗？")]))))

(defn query-form []
  (let [params (subscribe [:tag/query-params])]
    [:> Form
     [:> Form.Group
      [:> Form.Input {:label       "名称"
                      :inline      true
                      :on-change   #(dispatch [:tag/set-query-params {:name (du/event-value %)}])}]]
     [:div.button-center
      [:> Button {:basic    true
                  :icon "search"
                  :content  "查询"
                  :on-click #(dispatch [:tag/load-page @params])}]
      [:> Button {:color "green"
                  :icon "add"
                  :content  "新增"
                  :on-click #(navigate! "/tags/new")}]]]))

(defn list-table []
  (let [tags (subscribe [:tag/list])
        query-params (subscribe [:tag/query-params])
        pagination (subscribe [:tag/pagination])]
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
         (for [{:keys [id name description create_at update_at] :as tags} @tags]
           ^{:key tags}
           [:> Table.Row
            [:> Table.Cell 1]
            [:> Table.Cell name]
            [:> Table.Cell description]
            [:> Table.Cell (du/to-date-time create_at)]
            [:> Table.Cell (du/to-date-time update_at)]
            [:> Table.Cell
             [:div
              [:> Button {:color    "red"
                          :alt      "删除"
                          :icon     "delete"
                          :on-click #(do
                                       (dispatch [:tag/set-attr {:id id :name name}])
                                       (dispatch [:tag/set-delete-dialog true]))}]]]]))]]
     (if @tags
       [c/table-page :tag/load-page (merge @query-params @pagination)])]))

(defn home []
  [c/layout
   [:<>
    [query-form]
    [:> Divider]
    [delete-modal]
    [list-table]]])