(ns soul-talk.app-key.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [dispatch subscribe]]
            [soul-talk.common.views :as c]
            [soul-talk.routes :refer [navigate!]]
            ["semantic-ui-react" :refer [Form Button Table Divider Icon Container Card Input]]
            [soul-talk.utils :as utils]))

(defn- new-form []
  (let [app-key    (subscribe [:app-key/edit])
        user    (subscribe [:user])
        user-id (:id @user)
        _ (dispatch [:app-key/set-attr {:update_by user-id :create_by user-id}])]
    ^{:key "add-role-form"}
    [:> Form {:name       "add-role-form"}
     [:> Form.Input {:name      "name"
                     :label     "名称"
                     :inline    true
                     :required  true
                     :on-change #(dispatch [:app-key/set-attr {:name (utils/event-value %)}])}]
     [:> Form.Input {:name      "note"
                     :label     "备注"
                     :inline    true
                     :on-change #(dispatch [:app-key/set-attr {:note (utils/event-value %)}])}]
     [:div.button-center
      [:> Button {:on-click #(js/history.go -1)}
       "返回"]
      [:> Button {:color    "green"
                  :icon     "save"
                  :content  "保存"
                  :on-click #(dispatch [:app-key/update @app-key])}]]]))

(defn new []
  [c/layout [new-form]])

(defn- edit-form []
  (let [app-key (subscribe [:app-key/edit])
        user (subscribe [:user])
        menus (subscribe [:menus])
        user-id  (:id @user)]
    (let [{:keys [name note]} @app-key]
      [:> Form {:name       "add-role-form"}
       [:> Form.Input {:name          "name"
                       :inline        true
                       :label         "名称"
                       :required      true
                       :default-value name
                       :on-change     #(let [value (-> % .-target .-value)]
                                         (dispatch [:app-key/set-attr :name value]))}]
       [:> Form.Input {:name          "note"
                       :inline        true
                       :label         "备注"
                       :default-value note
                       :on-change     #(let [value (-> % .-target .-value)]
                                         (dispatch [:app-key/set-attr :note value]))}]
       [:> Divider]

       [:div.button-center
        [:> Button {:on-click #(js/history.go -1)}
         "返回"]
        [:> Button {:color    "green"
                    :content  "保存"
                    :on-click #(dispatch [:app-key/update @app-key])}]]])))

(defn edit []
  [c/layout [edit-form]])

(def ^:dynamic *delete-id* (r/atom 0))
(defn delete-dialog [id]
  (let [open (subscribe [:app-key/delete-dialog])]
    (if @open
      [c/confirm {:open    @open
                  :title    "删除角色"
                  :ok-text  "确认"
                  :on-close #(dispatch [:app-key/set-delete-dialog false])
                  :on-ok    #(do (dispatch [:app-key/set-delete-dialog false])
                                 (dispatch [:data-dices/delete id]))}
       "你确定要删除吗？"])))

(defn user-tree-items [{:keys [classes color bgColor menus checked-ids] :as props} ]
  (doall
    (for [menu menus]
      (let [{:keys [children id pid url name]} menu]
        ^{:key menu}
        (when-not (empty? children)
          (user-tree-items (assoc props :menus children :checked-ids checked-ids)))))))

(defn query-form []
  (let [query-params (subscribe [:app-key/query-params])]
    [:> Form {:name       "query-form"
              :size       "small"}
     [:> Form.Group
      [:> Form.Input {:name      "name"
                      :label     "名称"
                      :inline true
                      :on-change #(dispatch [:app-key/set-query-params :name (-> % .-target .-value)])}]]
     [:div.button-center
      [:> Button {:on-click #(dispatch [:app-key/load-page @query-params])}
       "搜索"]
      [:> Button {:color    "green"
                  :on-click #(navigate! "/app-key/new")}
       "新增"]]]))

(defn list-table []
  (let [app-keys (subscribe [:app-key/list])
        pagination (subscribe [:app-key/pagination])
        query-params (subscribe [:app-key/query-params])]
    [:<>
     [:> Table {:celled     true
                :selectable true
                :text-align "center"}
      [:> Table.Header
       [:> Table.Row
        [:> Table.HeaderCell "ID"]
        [:> Table.HeaderCell "Email"]
        [:> Table.HeaderCell "名称"]
        [:> Table.HeaderCell "备注"]
        [:> Table.HeaderCell "操作"]]]
      [:> Table.Body
       (doall
         (for [{:keys [id email name note] :as app-key} @app-keys]
           ^{:key app-key}
           [:> Table.Row
            [:> Table.Cell id]
            [:> Table.Cell email]
            [:> Table.Cell name]
            [:> Table.Cell note]
            [:> Table.Cell
             [:div
              [:> Button {:color    "green"
                          :icon     "edit"
                          :on-click #(navigate! (str "/app-key/" id "/edit"))}]
              [:> Button {:color    "red"
                          :icon     "delete"
                          :on-click (fn []
                                      (do
                                        (dispatch [:app-key/set-delete-dialog true])
                                        (dispatch [:app-key/set-attr app-key])))}]]]]))]]
     (if @app-keys
       [c/table-page :app-key/load-page (merge @query-params @pagination)])]))

(defn home []
  [c/layout
   [:<>
    [delete-dialog]
    [query-form]
    [list-table]]])




