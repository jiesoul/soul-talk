(ns soul-talk.app-key.views
  (:require [re-frame.core :refer [dispatch subscribe]]
            [soul-talk.common.views :as c]
            [soul-talk.routes :refer [navigate!]]
            ["semantic-ui-react" :refer [Form Button Table Divider Input Label Select Dropdown Grid]]
            [soul-talk.utils :as utils]
            [taoensso.timbre :as log]))

(defn- new-form []
  (let [app-key    (subscribe [:app-key/edit])
        user    (subscribe [:user])
        user-id (:id @user)
        _ (dispatch [:app-key/set-attr {:create_by user-id}])]
    [c/form-layout
     [:> Form {:name "add-app-key-form"}
      [:> Form.Input {:name      "name"
                      :label     "应用名称"
                      :required  true
                      :on-change #(dispatch [:app-key/set-attr {:app_name (utils/event-value %)}])}]
      [:> Form.Input {:required      true
                      :read-only     true
                      :default-value (:token @app-key)
                      :action        {:color    "teal"
                                      :type     "button"
                                      :on-click #(dispatch [:app-key/gen])
                                      :content  "生成"}}]
      [:div.button-center
       [:> Button {:on-click #(navigate! (str "/app-key"))}
        "返回"]
       [:> Button {:color    "green"
                   :icon     "save"
                   :content  "保存"
                   :on-click #(dispatch [:app-key/save @app-key])}]]]]))

(defn new []
  [c/layout [new-form]])

(def valid-options
  [{:key 1 :value 1 :text "有效" :selected true}
   {:key 0 :value 0 :text "无效"}])

(defn- edit-form []
  (let [app-key (subscribe [:app-key/edit])
        user (subscribe [:user])
        data-dices @(subscribe [:data-dices])
        options (utils/data->options (filter #(= "10" (:pid %)) data-dices) :id :name :id)
        {:keys [app_name token is_valid]} @app-key]
    [c/form-layout
     [:> Form {:name "edit-app-key-form"}
      [:> Form.Input {:name          "name"
                      :label         "名称"
                      :required      true
                      :default-value app_name
                      :on-change     #(let [value (-> % .-target .-value)]
                                        (dispatch [:app-key/set-attr :name value]))}]
      [:> Form.Input {:required      true
                      :read-only     true
                      :label "Token"
                      :default-value token
                      :action        {:color    "teal"
                                      :type     "button"
                                      :on-click #(dispatch [:app-key/gen])
                                      :content  "重新生成"}}]
      [:> Form.Select {:placeholder   "是否有效"
                       :label         "有效"
                       :options       options
                       :default-value is_valid
                       :on-change     #(dispatch [:app-key/set-attr {:is_valid (.-value %2)}])}]

      [:div.button-center
       [:> Button {:on-click #(navigate! (str "/app-key"))}
        "返回"]
       [:> Button {:color    "green"
                   :content  "保存"
                   :on-click #(dispatch [:app-key/update @app-key])}]]]]))

(defn edit []
  [c/layout [edit-form]])

(defn delete-dialog []
  (let [open (subscribe [:app-key/delete-dialog])
        app-key (subscribe [:app-key/edit])]
    (when @open
      [c/confirm {:open    @open
                  :title    "删除角色"
                  :ok-text  "确认"
                  :on-close #(dispatch [:app-key/set-delete-dialog false])
                  :on-ok    #(do (dispatch [:app-key/set-delete-dialog false])
                                 (dispatch [:app-key/delete (:id @app-key)]))}
       (str "你确定要删除 " (:app_name @app-key) " 吗？")])))

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
        [:> Table.HeaderCell "KEY"]
        [:> Table.HeaderCell "应用"]
        [:> Table.HeaderCell "有效"]
        [:> Table.HeaderCell "创建人"]
        [:> Table.HeaderCell "生成时间"]
        [:> Table.HeaderCell "刷新时间"]
        [:> Table.HeaderCell "操作"]]]
      [:> Table.Body
       (doall
         (for [{:keys [id token app_name valid create_by create_at refresh_at] :as app-key} @app-keys]
           ^{:key app-key}
           [:> Table.Row
            [:> Table.Cell id]
            [:> Table.Cell token]
            [:> Table.Cell app_name]
            [:> Table.Cell valid]
            [:> Table.Cell create_by]
            [:> Table.Cell (utils/to-date create_at)]
            [:> Table.Cell (utils/to-date refresh_at)]
            [:> Table.Cell
             [:div
              [:> Button {:color    "green"
                          :icon     "edit"
                          :on-click #(navigate! (str "/app-key/" id "/edit"))}]
              [:> Button {:color    "red"
                          :icon     "delete"
                          :on-click #(do
                                       (dispatch [:app-key/set-delete-dialog true])
                                       (dispatch [:app-key/set-attr app-key]))}]]]]))]]
     (when @app-keys
       [c/table-page :app-key/load-page (merge @query-params @pagination)])]))

(defn home []
  [c/layout
   [:<>
    [delete-dialog]
    [query-form]
    [list-table]]])




