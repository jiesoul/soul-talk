(ns soul-talk.role.views
  (:require [soul-talk.common.views :as c]
            [reagent.core :as r]
            [re-frame.core :as rf :refer [subscribe dispatch dispatch-sync]]
            [soul-talk.utils :as utils]
            [soul-talk.common.styles :as styles]
            [soul-talk.routes :refer [navigate!]]
            ["@material-ui/core" :as mui]
            ["@material-ui/icons" :as mui-icons]
            ["@material-ui/lab" :refer [TreeItem TreeView]]
            ["semantic-ui-react" :refer [Form Button Table Divider Icon Container Card Input]]))

(defn menu-tree-items [{:keys [classes color bgColor] :as props} menus role]
  (let [checked-ids (:menus-ids role)]
    (doall
      (for [menu menus]
        (let [{:keys [children id pid url]} menu]
          ^{:key menu}
          [:> TreeItem
           {:nodeId (str id)
            :label  (r/as-element
                      (let []
                        [:div
                         [:> mui/Checkbox {:checked   (if (contains? checked-ids id) true false)
                                           :on-change #(dispatch [:roles/checked-menu {:id id :pid pid} (-> % .-target .-checked)])}]
                         [:> mui/Button
                          (:name menu)]]))
            :on-label-click #()}
           (when-not (empty? children)
             (menu-tree-items props children role))])))))

(defn menu-tree-view [{:keys [classes] :as props}]
  (let [user (rf/subscribe [:user])
        menus (subscribe [:menus])
        menus-tree (utils/make-tree @menus)
        role (subscribe [:roles/edit])
        _ (dispatch [:roles/set-attr {:create_by (:id @user)}])]
    (fn []
      [:> Card
       [:> Card.Header "菜单列表"]
       [:> Card.Content
        [:> TreeView {
                      :multi-select          true
                      :default-collapse-icon (r/as-element [:> mui-icons/ExpandMore])
                      :default-expand-icon   (r/as-element [:> mui-icons/ChevronRight])
                      :default-end-icon      (r/as-element [:div {:style {:width 24}}])}
         (menu-tree-items props (:children menus-tree) @role)]]])))

(defn- add-form []
  (let [role    (subscribe [:roles/edit])
        user    (subscribe [:user])
        menus (subscribe [:menus])
        user-id (:id @user)
        _ (dispatch [:roles/set-attr {:update_by user-id :create_by user-id :menus-ids #{}}])]
    ^{:key "add-role-form"}
    [:> Form {:name       "add-role-form"
            :size "mini"}
     [:> Form.Group
      [:> Form.Input {:name      "name"
                      :label     "名称"
                      :inline    true
                      :required  true
                      :on-change #(dispatch [:roles/set-attr {:name (utils/event-value %)}])}]
      [:> Form.Input {:name      "note"
                      :label     "备注"
                      :inline    true
                      :on-change #(dispatch [:roles/set-attr {:note (utils/event-value %)}])}]]

     [:div
      (styles/styled-checkbox-list menu-tree-view)]
     [:div {:style {:text-align "center"}}
      [:> Button {:size     "mini"
                  :color    "green"
                  :icon     "save"
                  :content  "保存"
                  :on-click #(dispatch [:menus/update @role])}]
      [:> Button {:size     "mini"
                  :basic    true
                  :on-click #(js/history.go -1)}
       "返回"]]]))

(defn add []
  [c/layout [add-form]])

(defn- edit-form []
  (let [role (subscribe [:roles/edit])
        user (subscribe [:user])
        menus (subscribe [:menus])
        user-id  (:id @user)]
    (let [{:keys [name note]} @role]
      [:> Form {:name       "add-role-form"
                :size "mini"}
       [:> Form.Group
        [:> Form.Input {:name          "name"
                        :inline        true
                        :label         "名称"
                        :size          "small"
                        :required      true
                        :default-value name
                        :on-change     #(let [value (-> % .-target .-value)]
                                          (dispatch [:roles/set-attr :name value]))}]
        [:> Form.Input {:name          "note"
                        :inline        true
                        :label         "备注"
                        :default-value note
                        :on-change     #(let [value (-> % .-target .-value)]
                                          (dispatch [:roles/set-attr :note value]))}]]
       [:> Divider]
       (styles/styled-checkbox-list menu-tree-view)

       [:div {:style {:text-align "center"}}
        [:> Button {:basic    true
                    :size     "mini"
                    :color    "green"
                    :content  "保存"
                    :on-click #(do
                                 (dispatch [:menus/set-attr :update_by (:id @user)])
                                 (dispatch [:menus/update @role]))}]
        [:> Button {
                    :size     "mini"
                    :basic    true
                    :on-click #(js/history.go -1)}
         "返回"]]])))

(defn edit []
  [c/layout [edit-form]])

(def ^:dynamic *delete-id* (r/atom 0))
(defn delete-dialog [id]
  (let [open (subscribe [:roles/delete-dialog-open])]
    (if @open
      [c/modal {:open      @open
                 :title    "删除角色"
                 :ok-text  "确认"
                 :on-close #(dispatch [:roles/set-delete-dialog-open false])
                 :on-ok    #(do (dispatch [:roles/set-delete-dialog-open false])
                                (dispatch [:data-dices/delete id]))}
       "你确定要删除吗？"])))

(defn query-form []
  (let [query-params (subscribe [:roles/query-params])]
    (fn []
      [:> Form {:name       "query-form"
              :size       "mini"}
       [:> Form.Group
        [:> Form.Input {:name      "name"
                        :label     "名称"
                   :inline true
                        :on-change #(dispatch [:roles/set-query-params :name (-> % .-target .-value)])}]]
       [:div {:style {:text-align "right"}}
        [:> Button {:basic true
                    :color    "green"
                    :size "mini"
                    :icon "search"
                    :content "查询"
                    :on-click #(dispatch [:roles/load-page @query-params])}]
        [:> Button {:color    "red"
                    :basic true
                    :size "mini"
                    :icon "add"
                    :content "新增"
                    :on-click #(navigate! "/roles/add")}]]])))

(defn list-table []
  (let [roles (subscribe [:roles])
        pagination (subscribe [:roles/pagination])
        query-params (subscribe [:roles/query-params])]
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
              [:> Button {:color    "green"
                          :size     "mini"
                          :icon     "edit"
                          :basic true
                          :on-click #(navigate! (str "/roles/" id "/edit"))}]

              [:> Button {:color    "red"
                          :size     "mini"
                          :basic true
                          :icon     "delete"
                          :on-click (fn []
                                      (do
                                        (dispatch [:roles/set-delete-dialog-open true])
                                        (reset! *delete-id* id)))}]]]]))]]
     (when @roles
       [c/table-page :roles/load-page (merge @query-params @pagination)])]))

(defn home []
  [c/layout
   [:<>
    [delete-dialog]
    [query-form]
    [:> Divider]
    [list-table]]])
