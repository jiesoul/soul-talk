(ns soul-talk.role.views
  (:require [soul-talk.common.views :as c]
            [reagent.core :as r]
            [re-frame.core :as rf :refer [subscribe dispatch dispatch-sync]]
            [soul-talk.utils :as utils]
            [soul-talk.common.styles :as styles]
            [soul-talk.routes :refer [navigate!]]
            ["@material-ui/core" :as mui]
            ["@material-ui/icons" :as mui-icons]
            ["@material-ui/lab" :refer [TreeItem TreeView]]))

(defn add-form [{:keys [classes]}]
  (let [role    (subscribe [:roles/role])
        user    (subscribe [:user])
        open (subscribe [:roles/add-dialog-open])
        user-id (:id @user)]
    (if @open
      [c/dialog {:open     @open
                 :title    "添加角色"
                 :on-close (fn [e props]
                             (do
                               (dispatch [:roles/set-add-dialog-open false])))
                 :on-ok    #(let [role @role]
                              (assoc role :update_by user-id)
                              (dispatch [:roles/add role]))}
       [:form {:name       "add-role-form"
               :class-name (.-root classes)}
        [:> mui/TextField {:name       "name"
                           :label      "名称"
                           :size       "small"
                           :required   true
                           :full-width true
                           :rules      [{:required true}]
                           :on-change  #(let [value (-> % .-target .-value)]
                                          (dispatch [:roles/set-attr :name value]))}]
        [:> mui/TextField {:name       "note"
                           :label      "备注"
                           :size       "small"
                           :full-width true
                           :on-change  #(let [value (-> % .-target .-value)]
                                          (dispatch [:roles/set-attr :note value]))}]]])))
(defn edit-form [{:keys [classes]}]
  (let [role (subscribe [:roles/role])
        user (subscribe [:user])
        user-id  (:id @user)
        open (subscribe [:roles/edit-dialog-open])]
    (if @open
      (let [{:keys [name note]} @role]
        [c/dialog {:open     @open
                   :title    "编辑角色"
                   :on-close #(dispatch [:roles/set-edit-dialog-open false])
                   :on-ok    #(let [role @role]
                                (assoc role :update_by user-id)
                                (dispatch [:roles/update role]))}
         [:form {:name "add-role-form"
                 :class-name (.-root classes)}
          [:> mui/TextField {:name      "name"
                             :label     "名称"
                             :size "small"
                             :required true
                             :full-width true
                             :value name
                             :on-change #(let [value (-> % .-target .-value)]
                                           (dispatch [:roles/set-attr :name value]))}]
          [:> mui/TextField {:name      "note"
                             :label     "备注"
                             :size "small"
                             :value note
                             :full-width true
                             :on-change #(let [value (-> % .-target .-value)]
                                           (dispatch [:roles/set-attr :note value]))}]]]))))

(def ^:dynamic *delete-id* (r/atom 0))
(defn delete-dialog [id]
  (let [open (subscribe [:roles/delete-dialog-open])]
    (if @open
      [c/dialog {:open     @open
                 :title    "删除角色"
                 :ok-text  "确认"
                 :on-close #(dispatch [:roles/set-delete-dialog-open false])
                 :on-ok    #(do (dispatch [:roles/set-delete-dialog-open false])
                                (dispatch [:data-dices/delete id]))}
       [:> mui/DialogContentText "你确定要删除吗？"]])))

(defn menu-tree-items [{:keys [classes color bgColor menus checked-ids] :as props} ]
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
           (menu-tree-items (assoc props :menus children :checked-ids checked-ids)))]))))

(defn role-menus-form-dialog [{:keys [classes] :as props}]
  (let [role (subscribe [:roles/role])
        role-menus (subscribe [:roles/role-menus])
        open (subscribe [:roles/menus-dialog-open])
        menus (subscribe [:menus])]
    (if @open
      (fn []
        [c/dialog {:open     @open
                   :title    (str "角色：" (:name @role))
                   :on-close #(dispatch [:roles/set-menus-dialog-open false])
                   :on-ok    (fn [])}
         [:> mui/Paper {:class-name (.-root classes)}
          [:form {:id "role-menus-form"}
           [:> mui/Divider]
           [:> TreeView {:default-collapse-icon (r/as-element [:> mui-icons/ArrowDropDown])
                         :default-expand-icon   (r/as-element [:> mui-icons/ArrowRight])
                         :default-end-icon      (r/as-element [:div {:style {:width 24}}])}
            (menu-tree-items (assoc props :role @role :checked-ids (map :menu_id @role-menus) :menus (:children (utils/make-tree @menus))))]]]]))))

(defn query-form [{:keys [classes]}]
  (let [query-params (subscribe [:roles/query-params])
        {:keys [name]} @query-params]
    (fn []
      [:> mui/Paper {:class-name (.-paper classes)}
       [:form {:name       "query-form"
               :class-name (.-root classes)
               :size "small"}
        [:div
         [:> mui/TextField {:name      "name"
                            :label     "名称"
                            :value    name
                            :size "small"
                            :on-change #(dispatch [:roles/set-query-params :name (-> % .-target .-value)])}]]
        [:div {:class-name (.-buttons classes)}
         [:> mui/Button {:color    "primary"
                         :size     "small"
                         :variant  "outlined"
                         :type "reset"
                         :on-click #(dispatch [:roles/clean-query-params])}
          "重置"]
         [:> mui/Button {:variant  "outlined"
                         :size     "small"
                         :color    "primary"
                         :on-click #(dispatch [:roles/load-page @query-params])}
          "搜索"]
         [:> mui/Button {:color    "secondary"
                         :size     "small"
                         :variant  "outlined"
                         :on-click (fn []
                                     (dispatch [:roles/set-add-dialog-open true]))}
          "新增"]]]])))

(defn list-table [{:keys [classes]}]
  (let [roles (subscribe [:roles/list])
        pagination (subscribe [:roles/pagination])
        query-params (subscribe [:roles/query-params])]
    (fn []
      [:> mui/TableContainer {:class-name (.-paper classes)
                              :component  mui/Paper}
       [:> mui/Table {:class-name    (.-table classes)
                      :sticky-header true
                      :aria-label    "list-table"
                      :size          "small"}
        [:> mui/TableHead {:class-name (.-head classes)}
         [:> mui/TableRow {:class-name (.-head classes)}
          [:> mui/TableCell {:align "center"} "名称"]
          [:> mui/TableCell {:align "center"} "备注"]
          [:> mui/TableCell {:align "center"} "菜单"]
          [:> mui/TableCell {:align "center"} "操作"]
          ]]
        [:> mui/TableBody {:class-name (.-body classes)}
         (doall
           (for [{:keys [id name note] :as role} @roles]
             ^{:key role}
             [:> mui/TableRow {:class-name (.-row classes)}
              [:> mui/TableCell {:align "center"} name]
              [:> mui/TableCell {:align "center"} note]
              [:> mui/TableCell {:align "center"}
               [:div
                [:> mui/Button {:size     "small"
                                :color    "primary"
                                :on-click #(do
                                             (dispatch [:roles/load-role id])
                                             (dispatch [:menus/load-menus])
                                             (dispatch [:roles/load-role-menus id])
                                             (dispatch [:roles/set-menus-dialog-open true]))}
                 "明细"]]]
              [:> mui/TableCell {:align "center"}
               [:div
                [:> mui/IconButton {:color    "primary"
                                    :size     "small"
                                    :on-click (fn []
                                                (dispatch [:roles/load-role id])
                                                (dispatch [:roles/set-edit-dialog-open true]))}
                 [:> mui-icons/Edit]]

                [:> mui/IconButton {:color    "secondary"
                                    :size     "small"
                                    :style    {:margin "0 8px"}
                                    :on-click (fn []
                                                (do
                                                  (dispatch [:roles/set-delete-dialog-open true])
                                                  (reset! *delete-id* id)))}
                 [:> mui-icons/Delete]]]]]))]]
       (if @roles
         [c/table-page :roles/load-page (merge @query-params @pagination)])])))

(defn query-page
  [props]
  [c/layout props
   [:<>
    (styles/styled-edit-form add-form)
    (styles/styled-edit-form edit-form)
    (styles/styled-edit-form delete-dialog)
    (styles/styled-edit-form role-menus-form-dialog)
    (styles/styled-form query-form)
    (styles/styled-table list-table)
    ]])

(defn home []
  (styles/main query-page))
