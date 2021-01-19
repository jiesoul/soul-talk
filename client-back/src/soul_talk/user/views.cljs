(ns soul-talk.user.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf :refer [dispatch subscribe]]
            [soul-talk.common.styles :as styles]
            [soul-talk.common.views :as c]
            [soul-talk.utils :as utils]
            ["@material-ui/core" :as mui]
            ["@material-ui/icons" :refer [Edit Delete ArrowRight ArrowDropDown]]
            ["@material-ui/lab" :refer [TreeView TreeItem]]))

(defn change-pass-form [{:keys [classes]}]
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
      [:> mui/Paper {:class-name (.-paper classes)}
       [:form {:class-name (.-root classes)
               :id         "user-password-edit-form"}
        [:> mui/Input {:id         "username"
                       :read-only  true
                       :label      "用户名："
                       :value      (:name @pass-data)
                       :variant    "outlined"
                       :margin     "normal"
                       :full-width true
                       :size       "small"}]
        [:> mui/TextField {:id          "old-pass"
                           :name        "old-pass"
                           :placeholder "请输入旧密码"
                           :label       "旧密码："
                           :variant     "outlined"
                           :margin      "normal"
                           :type "password"
                           :required    true
                           :full-width  true
                           :size        "small"
                           :on-change   #(reset! old-password (.-target.value %))}]
        [:> mui/TextField {:id          "pass-new"
                           :name        "pass-new"
                           :placeholder "请输入新密码"
                           :type "password"
                           :label       "新密码："
                           :variant     "outlined"
                           :margin      "normal"
                           :required    true
                           :full-width  true
                           :size        "small"
                           :on-change   #(reset! new-password (.-target.value %))}]
        [:> mui/TextField {:id          "pass-confirm"
                           :name        "pass-confirm"
                           :placeholder "重复新密码"
                           :label       "新密码："
                           :type "password"
                           :variant     "outlined"
                           :margin      "normal"
                           :required    true
                           :full-width  true
                           :size        "small"
                           :on-change   #(reset! confirm-password (.-target.value %))}]
        [:div {:class-name (.-buttons classes)}
         [:> mui/Button {:type     "button"
                         :variant  "outlined"
                         :size     "small"
                         :color    "primary"
                         :on-click #(rf/dispatch [:users/change-password @pass-data])}
          "保存"]]]])))

(defn change-pass-page [props]
  [c/layout props
   (styles/styled-edit-form change-pass-form)])

(defn change-pass []
  (styles/styled-layout change-pass-page))

(defn user-profile-form [{:keys [classes]}]
  (let [user (rf/subscribe [:users/user])
        edited-user (r/atom @user)
        name (r/cursor edited-user [:name])]
    (fn []
      (if @user
        [:> mui/Paper {:class-name (.-paper classes)}
         [:form {:class-name (.-root classes)
                 :id         "user-profile-edit-form"}
          [:> mui/TextField {:id           "email"
                             :label "邮箱："
                             :disabled     true
                             :value        (:email @edited-user)
                             :read-only    true}]
          [:> mui/TextField {:variant    "outlined"
                             :margin     "normal"
                             :required   true
                             :full-width true
                             :size       "small"
                             :label      "名称"
                             :name       "name"
                             :id         "name"
                             :value      @name
                             :on-change  #(reset! name (-> % .-target .-value))}]

          [:div {:class-name (.-buttons classes)}
           [:> mui/Button {:type     "button"
                           :variant  "outlined"
                           :size     "small"
                           :color    "primary"
                           :on-click #(rf/dispatch [:users/user-profile @edited-user])}
            "保存"]]
          ]]))))

(defn user-profile-page [props]
  [c/layout props
   (styles/styled-edit-form user-profile-form)])

(defn user-profile []
  (styles/styled-layout user-profile-page))

(defn add-form [{:keys [classes]}]
  (let [user    (subscribe [:users/user])
        user    (subscribe [:user])
        open (subscribe [:users/add-dialog-open])
        user-id (:id @user)]
    (if @open
      [c/dialog {:open     @open
                 :title    "添加角色"
                 :on-close (fn [e props]
                             (do
                               (dispatch [:users/set-add-dialog-open false])))
                 :on-ok    #(let [user @user]
                              (assoc user :update_by user-id)
                              (dispatch [:users/add user]))}
       [:form {:name       "add-user-form"
               :class-name (.-root classes)}
        [:> mui/TextField {:name       "name"
                           :label      "名称"
                           :size       "small"
                           :required   true
                           :full-width true
                           :rules      [{:required true}]
                           :on-change  #(let [value (-> % .-target .-value)]
                                          (dispatch [:users/set-attr :name value]))}]
        [:> mui/TextField {:name       "note"
                           :label      "备注"
                           :size       "small"
                           :full-width true
                           :on-change  #(let [value (-> % .-target .-value)]
                                          (dispatch [:users/set-attr :note value]))}]]])))
(defn edit-form [{:keys [classes]}]
  (let [user (subscribe [:users/user])
        user (subscribe [:user])
        user-id  (:id @user)
        open (subscribe [:users/edit-dialog-open])]
    (if @open
      (let [{:keys [name note]} @user]
        [c/dialog {:open     @open
                   :title    "编辑角色"
                   :on-close #(dispatch [:users/set-edit-dialog-open false])
                   :on-ok    #(let [user @user]
                                (assoc user :update_by user-id)
                                (dispatch [:users/update user]))}
         [:form {:name "add-user-form"
                 :class-name (.-root classes)}
          [:> mui/TextField {:name      "name"
                             :label     "名称"
                             :size "small"
                             :required true
                             :full-width true
                             :value name
                             :on-change #(let [value (-> % .-target .-value)]
                                           (dispatch [:users/set-attr :name value]))}]
          [:> mui/TextField {:name      "note"
                             :label     "备注"
                             :size "small"
                             :value note
                             :full-width true
                             :on-change #(let [value (-> % .-target .-value)]
                                           (dispatch [:users/set-attr :note value]))}]]]))))

(def ^:dynamic *delete-id* (r/atom 0))
(defn delete-dialog [id]
  (let [open (subscribe [:users/delete-dialog-open])]
    (if @open
      [c/dialog {:open     @open
                 :title    "删除角色"
                 :ok-text  "确认"
                 :on-close #(dispatch [:users/set-delete-dialog-open false])
                 :on-ok    #(do (dispatch [:users/set-delete-dialog-open false])
                                (dispatch [:data-dices/delete id]))}
       [:> mui/DialogContentText "你确定要删除吗？"]])))

(defn user-tree-items [{:keys [classes color bgColor menus checked-ids] :as props} ]
  (doall
    (for [menu menus]
      (let [{:keys [children id pid url name]} menu]
        ^{:key menu}
        [:> TreeItem
         {:nodeId  (str id)
          :label   (r/as-element
                     (let []
                       [:div
                        [:> mui/Checkbox {:size      "small"
                                          :checked   (if (some #(= % id) checked-ids) true false)
                                          :on-change (fn [e]
                                                       (println (-> e .-target)))}]
                        [:> mui/Typography {:variant "inherit"}
                         name]]))
          :style   {"--tree-view-color"    color
                    "--tree-view-bg-color" bgColor}
          :classes {:root     (.-treeItemRoot classes)
                    :content  (.-treeItemContent classes)
                    :expanded (.-treeItemExpanded classes)
                    :selected (.-treeItemSelected classes)
                    :group    (.-treeItemGroup classes)
                    :label    (.-treeItemLabel classes)}
          }
         (when-not (empty? children)
           (user-tree-items (assoc props :menus children :checked-ids checked-ids)))]))))

(defn user-users-form-dialog [{:keys [classes] :as props}]
  (let [user (subscribe [:users/user])
        user-roles (subscribe [:users/user-roles])
        open (subscribe [:users/roles-dialog-open])
        menus (subscribe [:menus])]
    (if @open
      (fn []
        [c/dialog {:open     @open
                   :title    (str "角色：" (:name @user))
                   :on-close #(dispatch [:users/set-roles-dialog-open false])
                   :on-ok    (fn [])}
         [:> mui/Paper {:class-name (.-root classes)}
          [:form {:id "user-roles-form"}
           [:> mui/Divider]
           [:> TreeView {:default-collapse-icon (r/as-element [:> ArrowDropDown])
                         :default-expand-icon   (r/as-element [:> ArrowRight])
                         :default-end-icon      (r/as-element [:div {:style {:width 24}}])}
            (user-tree-items (assoc props :user @user :checked-ids (map :menu_id @user-roles) :menus (:children (utils/make-tree @menus))))]]]]))))

(defn query-form [{:keys [classes]}]
  (let [query-params (subscribe [:users/query-params])]
    [:> mui/Paper {:class-name (.-paper classes)}
     [:form {:name       "query-form"
             :class-name (.-root classes)
             :size       "small"}
      [:div
       [:> mui/TextField {:name      "name"
                          :label     "名称"
                          :size      "small"
                          :on-change #(dispatch [:users/set-query-params :name (-> % .-target .-value)])}]]
      [:div {:class-name (.-buttons classes)}
       [:> mui/Button {:color    "primary"
                       :size     "small"
                       :variant  "outlined"
                       :type     "reset"
                       :on-click #(dispatch [:users/clean-query-params])}
        "重置"]
       [:> mui/Button {:variant  "outlined"
                       :size     "small"
                       :color    "primary"
                       :on-click #(dispatch [:users/load-page @query-params])}
        "搜索"]
       [:> mui/Button {:color    "secondary"
                       :size     "small"
                       :variant  "outlined"
                       :on-click (fn []
                                   (dispatch [:users/set-add-dialog-open true]))}
        "新增"]]]]))

(defn list-table [{:keys [classes]}]
  (let [users (subscribe [:users/list])
        pagination (subscribe [:users/pagination])
        query-params (subscribe [:users/query-params])]
    (fn []
      [:> mui/TableContainer {:class-name (.-paper classes)
                              :component  mui/Paper}
       [:> mui/Table {:class-name    (.-table classes)
                      :sticky-header true
                      :aria-label    "list-table"
                      :size          "small"}
        [:> mui/TableHead {:class-name (.-head classes)}
         [:> mui/TableRow {:class-name (.-head classes)}
          [:> mui/TableCell {:align "center"} "ID"]
          [:> mui/TableCell {:align "center"} "Email"]
          [:> mui/TableCell {:align "center"} "名称"]
          [:> mui/TableCell {:align "center"} "备注"]
          [:> mui/TableCell {:align "center"} "操作"]]]
        [:> mui/TableBody {:class-name (.-body classes)}
         (doall
           (for [{:keys [id email name note] :as user} @users]
             ^{:key user}
             [:> mui/TableRow {:class-name (.-row classes)}
              [:> mui/TableCell {:align "center"} id]
              [:> mui/TableCell {:align "center"} email]
              [:> mui/TableCell {:align "center"} name]
              [:> mui/TableCell {:align "center"} note]
              [:> mui/TableCell {:align "center"}
               [:div
                [:> mui/IconButton {:color    "primary"
                                    :size     "small"
                                    :on-click (fn []
                                                (dispatch [:users/load-user id])
                                                (dispatch [:users/set-edit-dialog-open true]))}
                 [:> Edit]]

                [:> mui/IconButton {:color    "secondary"
                                    :size     "small"
                                    :style    {:margin "0 8px"}
                                    :on-click (fn []
                                                (do
                                                  (dispatch [:users/set-delete-dialog-open true])
                                                  (reset! *delete-id* id)))}
                 [:> Delete]]]]]))]]
       (if @users
         [c/table-page :users/load-page (merge @query-params @pagination)])])))

(defn query-page
  [props]
  [c/layout props
   [:<>
    (styles/styled-edit-form add-form)
    (styles/styled-edit-form edit-form)
    (styles/styled-edit-form delete-dialog)
    (styles/styled-edit-form user-users-form-dialog)
    (styles/styled-form query-form)
    (styles/styled-table list-table)]])

(defn home []
  (styles/styled-layout query-page))
