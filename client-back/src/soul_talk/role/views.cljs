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
;                                           :on-change #(dispatch [:roles/checked-menu {:id id :pid pid} (-> % .-target .-checked)])}]
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
        role (subscribe [:roles/edit])
        _ (dispatch [:roles/set-attr {:create_by (:id @user)}])]
    [:> Card
     [:> Card.Header "菜单列表"]
     [:> Card.Content
      ;[:> SourceTree {:tree-data tree-data}]
      ]]))

(defn- new-form []
  (let [role    (subscribe [:roles/edit])
        user    (subscribe [:user])
        menus (subscribe [:menus])
        user-id (:id @user)
        _ (dispatch [:roles/set-attr {:update_by user-id :create_by user-id :menus-ids #{}}])]
    ^{:key "add-role-form"}
    [:> Form {:name       "add-role-form"
            :size "small"}
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

     [menu-tree-view]
     [:div {:style {:text-align "center"}}
      [:> Button.Group {:size    "mini"
                        :compact true}
       [:> Button {:on-click #(js/history.go -1)}
        "返回"]
       [:> Button.Or]
       [:> Button {:color    "green"
                   :icon     "save"
                   :content  "保存"
                   :on-click #(dispatch [:menus/update @role])}]

       ]]]))

(defn new []
  [c/layout [new-form]])

(defn- edit-form []
  (let [role (subscribe [:roles/edit])
        user (subscribe [:user])
        menus (subscribe [:menus])
        user-id  (:id @user)]
    (let [{:keys [name note]} @role]
      [:> Form {:name       "add-role-form"
                :size "small"}
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

       [:div {:style {:text-align "center"}}
        [:> Button.Group {:size "mini"
                          :compact true}
         [:> Button {:on-click #(js/history.go -1)}
          "返回"]
         [:> Button.Or]
         [:> Button {:color    "green"
                     :content  "保存"
                     :on-click #(do
                                  (dispatch [:menus/set-attr :update_by (:id @user)])
                                  (dispatch [:menus/update @role]))}]]]])))

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
              :size       "small"}
       [:> Form.Group
        [:> Form.Input {:name      "name"
                        :label     "名称"
                   :inline true
                        :on-change #(dispatch [:roles/set-query-params :name (-> % .-target .-value)])}]]
       [:div {:style {:text-align "center"}}
        [:> Button.Group {:size "mini"
                          :compact true}
         [:> Button {:content  "查询"
                     :on-click #(dispatch [:roles/load-page @query-params])}]
         [:> Button.Or]
         [:> Button {:positive true
                     :content  "新增"
                     :on-click #(navigate! "/roles/new")}]]]])))

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
              [:> Button.Group {:size    "mini"
                                :compact true}
               [:> Button {:content  "编辑"
                           :positive true
                           :on-click #(navigate! (str "/roles/" id "/edit"))}]
               [:> Button.Or]
               [:> Button {:content  "删除"
                           :negative true
                           :on-click (fn []
                                       (do
                                         (dispatch [:roles/set-delete-dialog-open true])
                                         (reset! *delete-id* id)))}]]]]]))]]
     (when @roles
       [c/table-page :roles/load-page (merge @query-params @pagination)])]))

(defn home []
  [c/layout
   [:<>
    [delete-dialog]
    [query-form]
    [:> Divider]
    [list-table]]])
