(ns soul-talk.user.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf :refer [dispatch subscribe]]
            [soul-talk.common.styles :as styles]
            [soul-talk.common.views :as c]
            [soul-talk.utils :as utils]
            ["semantic-ui-react" :refer [Form Button Table Divider Icon Container Card Input]]))

(defn change-pass-form []
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
                      :value      (:name @pass-data)
                      :full-width true
                      :size       "small"}]
       [:> Form.Input {:id          "old-pass"
                       :name        "old-pass"
                       :placeholder "请输入旧密码"
                       :label       "旧密码："
                       :type        "password"
                       :required    true
                       :size        "small"
                       :on-change   #(reset! old-password (.-target.value %))}]
       [:> Form.Input {:id          "pass-new"
                       :name        "pass-new"
                       :placeholder "请输入新密码"
                       :type        "password"
                       :label       "新密码："
                       :required    true
                       :size        "small"
                       :on-change   #(reset! new-password (.-target.value %))}]
       [:> Form.Input {:id          "pass-confirm"
                       :name        "pass-confirm"
                       :placeholder "重复新密码"
                       :label       "新密码："
                       :type        "password"
                       :required    true
                       :size        "small"
                       :on-change   #(reset! confirm-password (.-target.value %))}]
       [:div {:style {:text-align "center"}}
        [:> Button {:type     "button"
                    :variant  "outlined"
                    :size     "small"
                    :color    "primary"
                    :on-click #(rf/dispatch [:users/change-password @pass-data])}
         "保存"]]])))

(defn change-pass []
  [c/layout [change-pass-form]])

(defn user-profile-form []
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
                         :size       "small"
                         :label      "名称"
                         :name       "name"
                         :id         "name"
                         :value      @name
                         :on-change  #(reset! name (-> % .-target .-value))}]

         [:div {:style {:text-align "center"}}
          [:> Button {:type     "button"
                      :variant  "outlined"
                      :size     "small"
                      :color    "primary"
                      :on-click #(rf/dispatch [:users/user-profile @edited-user])}
           "保存"]]
         ]))))

(defn user-profile []
  [c/layout [user-profile-form]])

(defn add-form []
  (let [user    (subscribe [:users/user])
        user    (subscribe [:user])
        open (subscribe [:users/add-dialog-open])
        user-id (:id @user)]
    [:> Form {:name       "add-user-form"}
     [:> Form.Input {:name       "name"
                     :label      "名称"
                     :size       "small"
                     :required   true
                     :full-width true
                     :rules      [{:required true}]
                     :on-change  #(let [value (-> % .-target .-value)]
                                    (dispatch [:users/set-attr :name value]))}]
     [:> Form.Input {:name       "note"
                     :label      "备注"
                     :size       "small"
                     :full-width true
                     :on-change  #(let [value (-> % .-target .-value)]
                                    (dispatch [:users/set-attr :note value]))}]]))
(defn edit-form []
  (let [user (subscribe [:users/user])
        user (subscribe [:user])
        user-id  (:id @user)
        open (subscribe [:users/edit-dialog-open])]
    (let [{:keys [name note]} @user]
      [:form {:name       "add-user-form"}
       [:> Form.Input {:name       "name"
                       :label      "名称"
                       :size       "small"
                       :required   true
                       :full-width true
                       :value      name
                       :on-change  #(let [value (-> % .-target .-value)]
                                      (dispatch [:users/set-attr :name value]))}]
       [:> Form.Input {:name       "note"
                       :label      "备注"
                       :size       "small"
                       :value      note
                       :full-width true
                       :on-change  #(let [value (-> % .-target .-value)]
                                      (dispatch [:users/set-attr :note value]))}]])))

(def ^:dynamic *delete-id* (r/atom 0))
(defn delete-dialog [id]
  (let [open (subscribe [:users/delete-dialog-open])]
    (if @open
      [c/modal {:open      @open
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

(defn user-users-form-dialog []
  (let [user (subscribe [:users/user])
        user-roles (subscribe [:users/user-roles])
        open (subscribe [:users/roles-dialog-open])
        menus (subscribe [:menus])]
    (if @open
      (fn []
        [:> Form {:id "user-roles-form"}
         ;(user-tree-items (assoc props :user @user :checked-ids (map :menu_id @user-roles)
         ;                              :menus (:children (utils/make-tree @menus))))
         ]
        ))))

(defn query-form []
  (let [query-params (subscribe [:users/query-params])]
    [:> Form {:name       "query-form"
            :size       "mini"}
     [:> Form.Group
      [:> Form.Input {:name      "name"
                      :label     "名称"
                      :inline true
                      :on-change #(dispatch [:users/set-query-params :name (-> % .-target .-value)])}]]
     [:div {:style {:text-align "center"}}
      [:> Button {:basic true
                  :size     "mini"
                  :on-click #(dispatch [:users/load-page @query-params])}
       "搜索"]
      [:> Button {:color    "green"
                  :basic true
                  :size     "mini"
                  :on-click (fn []
                              (dispatch [:users/set-add-dialog-open true]))}
       "新增"]]]))

(defn list-table []
  (let [users (subscribe [:users/list])
        pagination (subscribe [:users/pagination])
        query-params (subscribe [:users/query-params])]
    [:<>
     [:> Table {:celled     true
                :selectable true
                :size       "mini"
                :text-align "center"}
      [:> Table.Header
       [:> Table.Row
        [:> Table.HeaderCell {:align "center"} "ID"]
        [:> Table.HeaderCell {:align "center"} "Email"]
        [:> Table.HeaderCell {:align "center"} "名称"]
        [:> Table.HeaderCell {:align "center"} "备注"]
        [:> Table.HeaderCell {:align "center"} "操作"]]]
      [:> Table.Body
       (doall
         (for [{:keys [id email name note] :as user} @users]
           ^{:key user}
           [:> Table.Row
            [:> Table.Cell {:align "center"} id]
            [:> Table.Cell {:align "center"} email]
            [:> Table.Cell {:align "center"} name]
            [:> Table.Cell {:align "center"} note]
            [:> Table.Cell {:align "center"}
             [:div
              [:> Button {:basic true
                          :color    "green"
                          :size     "mini"
                          :icon     "edit"
                          :on-click (fn []
                                      (dispatch [:users/load-user id])
                                      (dispatch [:users/set-edit-dialog-open true]))}]

              [:> Button {:color    "red"
                          :basic true
                          :size     "mini"
                          :icon     "delete"
                          :style    {:margin "0 8px"}
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
