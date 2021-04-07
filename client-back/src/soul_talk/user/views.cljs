(ns soul-talk.user.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf :refer [dispatch subscribe]]
            [soul-talk.routes :refer [navigate!]]
            [soul-talk.common.views :as c]
            [soul-talk.utils :as utils]
            ["semantic-ui-react" :refer [Form Button Table Divider]]))

(defn password-form []
  (let [user (rf/subscribe [:user])
               pass-data (r/atom {:id (:id @user)
                                  :email (:email @user)
                                  :old-password ""
                                  :new-password ""
                                  :confirm-password ""})
               old-password (r/cursor pass-data [:old-password])
               new-password (r/cursor pass-data [:new-password])
               confirm-password (r/cursor pass-data [:confirm-password])]
    (when @user
      [c/form-layout
       [:> Form {:id "user-password-edit-form"}
        [:> Form.Input {:id            "username"
                        :read-only     true
                        :label         "用户名："
                        :default-value (:name @user)
                        :full-width    true}]
        [:> Form.Input {:id          "old-pass"
                        :name        "old-pass"
                        :placeholder "请输入旧密码"
                        :label       "旧密码："
                        :type        "password"
                        :required    true
                        :on-change   #(reset! old-password (.-target.value %))}]
        [:> Form.Input {:id          "pass-new"
                        :name        "pass-new"
                        :placeholder "请输入新密码"
                        :type        "password"
                        :label       "新密码："
                        :required    true
                        :on-change   #(reset! new-password (.-target.value %))}]
        [:> Form.Input {:id          "pass-confirm"
                        :name        "pass-confirm"
                        :placeholder "重复新密码"
                        :label       "新密码："
                        :type        "password"
                        :required    true
                        :on-change   #(reset! confirm-password (.-target.value %))}]
        [:div.button-center
         [:> Button {:on-click #(navigate! "/users")} "返回"]
         [:> Button {:positive true
                     :on-click #(rf/dispatch [:user/change-password @pass-data])}
          "保存"]]]])))

(defn password []
  [c/layout [password-form]])

(defn profile-form []
  (let [user (rf/subscribe [:user])
        edited-user (r/atom @user)
        name (r/cursor edited-user [:name])]
    (when @user
      [c/form-layout
       [:> Form {:id "user-profile-edit-form"}
        [:> Form.Input {:id        "email"
                        :label     "邮箱："
                        :default-value     (:email @edited-user)
                        :read-only true}]
        [:> Form.Input {:required  true
                        :label     "名称"
                        :name      "name"
                        :id        "name"
                        :default-value     @name
                        :on-change #(reset! name (-> % .-target .-value))}]

        [:div {:style {:text-align "center"}}
         [:> Button {:positive true
                     :on-click #(rf/dispatch [:user/user-profile @edited-user])}
          "保存"]]]])))

(defn profile []
  [c/layout [profile-form]])

(defn- new-form []
  (let [user    (subscribe [:user/edit])
        login-user    (subscribe [:user])
        ;; menus (subscribe [:menus])
        user-id (:id @login-user)
        _ (dispatch [:user/set-attr {:update_by user-id :create_by user-id :menus-ids #{}}])]
    ^{:key "add-role-form"}
    [c/form-layout
     [:> Form {:name "add-role-form"}
      [:> Form.Input {:name      "name"
                      :label     "名称"
                      :required  true
                      :on-change #(dispatch [:user/set-attr {:name (utils/event-value %)}])}]
      [:> Form.Input {:name      "note"
                      :label     "备注"
                      :on-change #(dispatch [:user/set-attr {:note (utils/event-value %)}])}]

      [:div.button-center
       [:> Button {:on-click #(js/history.go -1)}
        "返回"]
       [:> Button {:color    "green"
                   :icon     "save"
                   :content  "保存"
                   :on-click #(dispatch [:user/save @user])}]]]]))

(defn new []
  [c/layout [new-form]])

(defn- edit-form []
  (let [user (subscribe [:user/edit])
        login-user (subscribe [:user])
        ;; menus (subscribe [:menus])
        user-id  (:id @login-user)
        _ (dispatch [:user/set-attr {:update_by user-id}])
        {:keys [name note]} @user]
    [c/form-layout
     [:> Form {:name "edit-user-form"}
      [:> Form.Input {:name          "name"
                      :label         "名称"
                      :required      true
                      :default-value name
                      :on-change     #(let [value (-> % .-target .-value)]
                                        (dispatch [:user/set-attr {:name value}]))}]
      [:> Form.Input {:name          "note"
                      :label         "备注"
                      :default-value note
                      :on-change     #(let [value (-> % .-target .-value)]
                                        (dispatch [:user/set-attr {:note value}]))}]
      [:> Divider]

      [:div.button-center
       [:> Button {:on-click #(js/history.go -1)}
        "返回"]
       [:> Button {:color    "green"
                   :content  "保存"
                   :on-click #(dispatch [:user/update @user])}]]]]))

(defn edit []
  [c/layout [edit-form]])

(defn delete-dialog [id]
  (let [open (subscribe [:user/delete-dialog])]
    (when @open
      [c/confirm {:open    @open
                 :title    "删除角色"
                 :ok-text  "确认"
                 :on-close #(dispatch [:user/set-delete-dialog false])
                 :on-ok    #(do (dispatch [:user/set-delete-dialog false])
                                (dispatch [:user/delete id]))}
       "你确定要删除吗？"])))

(defn user-tree-items [{:keys [menus checked-ids] :as props} ]
  (doall
    (for [menu menus]
      (let [{:keys [children]} menu]
        ^{:key menu}
        (when-not (empty? children)
          (user-tree-items (assoc props :menus children :checked-ids checked-ids)))))))

(defn query-form []
  (let [query-params (subscribe [:user/query-params])]
    [:> Form {:name       "query-form"
            :size       "small"}
     [:> Form.Group
      [:> Form.Input {:name      "name"
                      :label     "名称"
                      :inline    true
                      :on-change #(dispatch [:user/set-query-params {:name (-> % .-target .-value)}])}]]
     [:div.button-center
      [:> Button {:on-click #(dispatch [:user/load-page @query-params])}
       "搜索"]
      [:> Button {:color    "green"
                  :on-click #(navigate! "/user/new")}
       "新增"]]])
  )

(defn list-table []
  (let [users (subscribe [:user/list])
        pagination (subscribe [:user/pagination])
        query-params (subscribe [:user/query-params])]
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
         (for [{:keys [id email name note] :as user} @users]
           ^{:key user}
           [:> Table.Row
            [:> Table.Cell id]
            [:> Table.Cell email]
            [:> Table.Cell name]
            [:> Table.Cell note]
            [:> Table.Cell
             [:div
              [:> Button {:color    "green"
                          :icon     "edit"
                          :on-click #(navigate! (str "/user/" id "/edit"))}]
              [:> Button {:color    "red"
                          :icon     "delete"
                          :on-click #(do
                                       (dispatch [:user/set-delete-dialog true])
                                       (dispatch [:user/set-attr user]))}]]]]))]]
     (when @users
       [c/table-page :user/load-page (merge @query-params @pagination)])])
  )

(defn home []
  [c/layout
   [:<>
    [delete-dialog]
    [query-form]
    [list-table]]])

(defn auth-key-query-form []
  (let [query-params (subscribe [:user/auth-key-query-params])]
    [:> Form {:name       "query-form"
              :size       "small"}
     [:> Form.Group
      [:> Form.Input {:name      "name"
                      :label     "名称"
                      :inline true
                      :on-change #(dispatch [:user/set-auth-key-query-params {:name (-> % .-target .-value)}])}]]
     [:div.button-center
      [:> Button {:on-click #(dispatch [:user/load-auth-key-page @query-params])}
       "搜索"]]]))

(defn auth-key-list-table []
  (let [auth-keys (subscribe [:user/auth-key-list])
        pagination (subscribe [:user/auth-key-pagination])
        query-params (subscribe [:user/auth-key-query-params])]
    [:<>
     [:> Table {:celled     true
                :selectable true
                :text-align "center"}
     [:> Table.Header
     [:> Table.Row
      [:> Table.HeaderCell "ID"]
        [:> Table.HeaderCell "KEY"]
        [:> Table.HeaderCell "用户ID"]
        [:> Table.HeaderCell "用户名称"]
        [:> Table.HeaderCell "创建时间"]
        [:> Table.HeaderCell "刷新时间"]]]
      [:> Table.Body
       (doall
        (for [{:keys [id token user_id name create_at refresh_at] :as auth-key} @auth-keys]
          ^{:key auth-key}
          [:> Table.Row
           [:> Table.Cell id]
           [:> Table.Cell token]
           [:> Table.Cell user_id]
           [:> Table.Cell name]
           [:> Table.Cell (utils/to-date-time create_at)]
           [:> Table.Cell (utils/to-date-time refresh_at)]]))]]
     (when @auth-keys
       [c/table-page :user/load-auth-key-page (merge @query-params @pagination)])]))

(defn auth-key-home []
  [c/layout
   [:<>
    [auth-key-query-form]
    [auth-key-list-table]]])
