(ns soul-talk.role.views
  (:require [soul-talk.common.views :as c]
            [reagent.core :as r]
            [re-frame.core :as rf :refer [subscribe dispatch dispatch-sync]]
            [soul-talk.utils :as utils]
            [soul-talk.routes :refer [navigate!]]
            ["semantic-ui-react" :refer [Form Button Table Divider Icon Container Card Input]]))

;(defn menu-tree-items [menus role]
;  (let [checked-ids (:menus-ids role)]
;    (doall
;      (for [menu menus]
;        (let [{:keys [children id pid url]} menu]
;          ^{:key menu}
;          [:> TreeItem
;           {:nodeId (str id)
;            :label  (r/as-element
;                      (let []
;                        [:div
;                         [:> mui/Checkbox {:checked   (if (contains? checked-ids id) true false)
;                                           :on-change #(dispatch [:role/checked-menu {:id id :pid pid} (-> % .-target .-checked)])}]
;                         [:> Button
;                          (:name menu)]]))
;            :on-label-click #()}
;           (when-not (empty? children)
;             (menu-tree-items children role))])))))

(defn menu-tree-view []
  (let [user (rf/subscribe [:user])
        menus (subscribe [:menus])
        tree-data (map #(clojure.set/rename-keys % []) menus)
        menus-tree (utils/make-tree @menus)
        role (subscribe [:role/edit])
        _ (dispatch [:role/set-attr {:create_by (:id @user)}])]
    [:> Card {:fluid true}
     [:> Card.Header "菜单列表"]
     [:> Card.Content
      ;[:> SourceTree {:tree-data tree-data}]
      ]]))

(defn- new-form []
  (let [role    (subscribe [:role/edit])
        user    (subscribe [:user])
        menus (subscribe [:menus])
        role-menus (:menus @role)
        user-id (:id @user)
        _ (dispatch [:role/set-attr {:update_by user-id :create_by user-id :menus-ids #{}}])]
    ^{:key "add-role-form"}
    [c/form-layout
     [:> Form {:name "add-role-form"}
      [:> Form.Input {:name      "name"
                      :label     "名称"
                      :required  true
                      :on-change #(dispatch [:role/set-attr {:name (utils/event-value %)}])}]
      [:> Form.Input {:name      "note"
                      :label     "备注"
                      :on-change #(dispatch [:role/set-attr {:note (utils/event-value %)}])}]

      [menu-tree-view]
      [:div.button-center
       [:> Button {:on-click #(navigate! (str "/role"))}
        "返回"]
       [:> Button {:color    "green"
                   :icon     "save"
                   :content  "保存"
                   :on-click #(dispatch [:role/save @role])}]

       ]]]))

(defn new []
  [c/layout [new-form]])

(defn- edit-form []
  (let [user (subscribe [:user])
        role (subscribe [:role/edit])
        _ (dispatch [:menus/set-attr :update_by (:id @user)])
        menus (subscribe [:menus])
        user-id  (:id @user)]
    (let [{:keys [name note]} @role]
      [c/form-layout
       [:> Form {:name "add-role-form"}
        [:> Form.Input {:name          "name"
                        :inline        true
                        :label         "名称"
                        :size          "small"
                        :required      true
                        :default-value name
                        :on-change     #(let [value (-> % .-target .-value)]
                                          (dispatch [:role/set-attr :name value]))}]
        [:> Form.Input {:name          "note"
                        :inline        true
                        :label         "备注"
                        :default-value note
                        :on-change     #(let [value (-> % .-target .-value)]
                                          (dispatch [:role/set-attr :note value]))}]
        [:> Divider]

        [:div {:style {:text-align "center"}}
         [:> Button {:on-click #(navigate! (str "/role"))}
          "返回"]
         [:> Button {:color    "green"
                     :content  "保存"
                     :on-click #(dispatch [:role/update @role])}]]]])))

(defn edit []
  [c/layout [edit-form]])

(defn delete-dialog [id]
  (let [open (subscribe [:role/delete-dialog])
        role (subscribe [:role/edit])]
    (if @open
      [c/confirm {:open   @open
                :title    "删除角色"
                :ok-text  "确认"
                :on-close #(dispatch [:role/set-delete-dialog false])
                :on-ok    #(do (dispatch [:role/set-delete-dialog false])
                               (dispatch [:data-dices/delete (:id @role)]))}
       (str "你确定要删除角色 " (:name @role) " 吗？")])))

(defn query-form []
  (let [query-params (subscribe [:role/query-params])]
    (fn []
      [:> Form
       [:> Form.Group
        [:> Form.Input {:name      "name"
                        :label     "名称"
                        :inline true
                        :on-change #(dispatch [:role/set-query-params :name (-> % .-target .-value)])}]]
       [:div.button-center
        [:> Button {:content  "查询"
                    :on-click #(dispatch [:role/load-page @query-params])}]
        [:> Button {:positive true
                    :content  "新增"
                    :on-click #(navigate! "/role/new")}]]])))

(defn list-table []
  (let [roles (subscribe [:role/list])
        pagination (subscribe [:role/pagination])
        query-params (subscribe [:role/query-params])]
    [:div
     [:> Table {:celled     true
                :selectable true
                :size       "small"
                :text-align "center"}
      [:> Table.Header
       [:> Table.Row
        [:> Table.HeaderCell "名称"]
        [:> Table.HeaderCell "备注"]
        [:> Table.HeaderCell "操作"]]]
      [:> Table.Body
       (doall
         (for [{:keys [id name note] :as role} @roles]
           ^{:key role}
           [:> Table.Row
            [:> Table.Cell name]
            [:> Table.Cell note]
            [:> Table.Cell
             [:div
              [:> Button {:icon "edit"
                          :positive true
                          :on-click #(navigate! (str "/role/" id "/edit"))}]
              [:> Button {:icon "delete"
                          :negative true
                          :on-click (fn []
                                      (do
                                        (dispatch [:role/set-attr role])
                                        (dispatch [:role/set-delete-dialog true])))}]]]]))]]
     (when @roles
       [c/table-page :role/load-page (merge @query-params @pagination)])]))

(defn home []
  [c/layout
   [:<>
    [delete-dialog]
    [query-form]
    [:> Divider]
    [list-table]]])
