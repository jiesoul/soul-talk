(ns soul-talk.user.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf :refer [dispatch subscribe]]
            [soul-talk.routes :refer [navigate!]]
            [soul-talk.common.views :as c]
            [soul-talk.utils :as utils]
            ["semantic-ui-react" :refer [Form Button Table Divider Icon Container Card Input]]))

(defn password-form []
  (let [user (rf/subscribe [:users/user])
               pass-data (r/atom {:id (:id @user)
                                  :email (:email @user)
                                  :old-password ""
                                  :new-password ""
                                  :confirm-password ""})
               old-password (r/cursor pass-data [:old-password])
               new-password (r/cursor pass-data [:new-password])
               confirm-password (r/cursor pass-data [:confirm-password])]
    (if @user
      [:> Form {:id         "user-password-edit-form"}
       [:> Form.Input {:id         "username"
                      :read-only  true
                      :label      "用户名："
                      :default-value      (:name @user)
                      :full-width true}]
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
                    :on-click #(rf/dispatch [:users/change-password @pass-data])}
         "保存"]]])))

(defn password []
  [c/layout [password-form]])

(defn profile-form []
  (let [user (rf/subscribe [:users/user])
        edited-user (r/atom @user)
        name (r/cursor edited-user [:name])]
    (fn []
      (if @user
        [:> Form {:id         "user-profile-edit-form"}
         [:> Form.Input {:id        "email"
                         :label     "邮箱："
                         :disabled  true
                         :value     (:email @edited-user)
                         :read-only true}]
         [:> Form.Input {:variant    "outlined"
                         :required   true
                         :label      "名称"
                         :name       "name"
                         :id         "name"
                         :value      @name
                         :on-change  #(reset! name (-> % .-target .-value))}]

         [:div {:style {:text-align "center"}}
          [:> Button {:positive true
                      :on-click #(rf/dispatch [:users/user-profile @edited-user])}
           "保存"]]]))))

(defn profile []
  [c/layout [profile-form]])

(defn- new-form []
  (let [role    (subscribe [:users/edit])
        user    (subscribe [:user])
        menus (subscribe [:menus])
        user-id (:id @user)
        _ (dispatch [:users/set-attr {:update_by user-id :create_by user-id :menus-ids #{}}])]
    ^{:key "add-role-form"}
    [:> Form {:name       "add-role-form"}
     [:> Form.Input {:name      "name"
                     :label     "名称"
                     :inline    true
                     :required  true
                     :on-change #(dispatch [:users/set-attr {:name (utils/event-value %)}])}]
     [:> Form.Input {:name      "note"
                     :label     "备注"
                     :inline    true
                     :on-change #(dispatch [:users/set-attr {:note (utils/event-value %)}])}]

     [:div.button-center
      [:> Button {:on-click #(js/history.go -1)}
       "返回"]
      [:> Button {:color    "green"
                  :icon     "save"
                  :content  "保存"
                  :on-click #(dispatch [:users/update @role])}]]]))

(defn new []
  [c/layout [new-form]])

(defn- edit-form []
  (let [user (subscribe [:users/edit])
        user (subscribe [:user])
        menus (subscribe [:menus])
        user-id  (:id @user)]
    (let [{:keys [name note]} @user]
      [:> Form {:name       "add-role-form"}
       [:> Form.Input {:name          "name"
                       :inline        true
                       :label         "名称"
                       :required      true
                       :default-value name
                       :on-change     #(let [value (-> % .-target .-value)]
                                         (dispatch [:users/set-attr :name value]))}]
       [:> Form.Input {:name          "note"
                       :inline        true
                       :label         "备注"
                       :default-value note
                       :on-change     #(let [value (-> % .-target .-value)]
                                         (dispatch [:users/set-attr :note value]))}]
       [:> Divider]

       [:div.button-center
        [:> Button {:on-click #(js/history.go -1)}
         "返回"]
        [:> Button {:color    "green"
                    :content  "保存"
                    :on-click #(dispatch [:users/update @user])}]]])))

(defn edit []
  [c/layout [edit-form]])

(def ^:dynamic *delete-id* (r/atom 0))
(defn delete-dialog [id]
  (let [open (subscribe [:users/delete-dialog-open])]
    (if @open
      [c/confirm {:open    @open
                 :title    "删除角色"
                 :ok-text  "确认"
                 :on-close #(dispatch [:users/set-delete-dialog-open false])
                 :on-ok    #(do (dispatch [:users/set-delete-dialog-open false])
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
  (let [query-params (subscribe [:users/query-params])]
    [:> Form {:name       "query-form"
            :size       "small"}
     [:> Form.Group
      [:> Form.Input {:name      "name"
                      :label     "名称"
                      :inline true
                      :on-change #(dispatch [:users/set-query-params :name (-> % .-target .-value)])}]]
     [:div.button-center
      [:> Button {:on-click #(dispatch [:users/load-page @query-params])}
       "搜索"]
      [:> Button {:color    "green"
                  :on-click #(navigate! "/users/new")}
       "新增"]]]))

(defn list-table []
  (let [users (subscribe [:users/list])
        pagination (subscribe [:users/pagination])
        query-params (subscribe [:users/query-params])]
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
                          :on-click #(navigate! (str "/users/" id "/edit"))}]
              [:> Button {:color    "red"
                          :icon     "delete"
                          :on-click (fn []
                                      (do
                                        (dispatch [:users/set-delete-dialog-open true])
                                        (reset! *delete-id* id)))}]]]]))]]
     (if @users
       [c/table-page :users/load-page (merge @query-params @pagination)])]))

(defn home []
  [c/layout
   [:<>
    [delete-dialog]
    [query-form]
    [list-table]]])



(defn auth-keys []
  )
