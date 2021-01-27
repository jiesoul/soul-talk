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

(defn menu-tree-items [{:keys [menus checked-ids]} ]
  (doall
    (for [{:keys [children id pid name] :as menu} menus]
      [:> TreeItem
       {:nodeId id
        :label name}
       (when-not (empty? children)
         (menu-tree-items {:menus       children
                           :checked-ids checked-ids}))])))

(defn role-menus [{:keys [classes] :as props}])

(defn- add-form [{:keys [classes]}]
  (let [role    (subscribe [:roles/edit])
        user    (subscribe [:user])
        menus (subscribe [:menus])
        user-id (:id @user)
        _ (dispatch [:roles/set-attr {:update_by user-id :create_by user-id :menus-ids #{}}])]
    ^{:key "add-role-form"}
    [:> mui/Paper {:class-name (.-paper classes)}
     [:form {:name       "add-role-form"
             :class-name (.-root classes)}
      [:> mui/TextField {:name       "name"
                         :label      "名称"
                         :size       "small"
                         :required   true
                         :full-width true
                         :rules      [{:required true}]
                         :on-change  #(dispatch [:roles/set-attr {:name (utils/event-value %)}])}]
      [:> mui/TextField {:name       "note"
                         :label      "备注"
                         :size       "small"
                         :full-width true
                         :on-change  #(dispatch [:roles/set-attr {:note (utils/event-value %)}])}]

      [:> mui/Divider]
      [:> mui/Typography "菜单列表"]
      [:> TreeView {:default-collapse-icon mui-icons/ExpandMore
                    :default-expand-icon   mui-icons/ChevronRight}
       ;(menu-tree-items {:checked-ids (:menus-ids @role)
       ;                  :menus       (:children (utils/make-tree @menus))})
       ]

      [:div {:style      {:margin "normal"}
             :class-name (.-buttons classes)}
       [:> mui/Button {:type     "button"
                       :variant  "outlined"
                       :size     "small"
                       :color    "primary"
                       :on-click #(dispatch [:menus/update @role])}
        "保存"]
       [:> mui/Button {:type     "button"
                       :variant  "outlined"
                       :size     "small"
                       :color    "secondary"
                       :on-click #(js/history.go -1)}
        "返回"]]]]))

(defn- add-page [props]
  [c/layout props
   (styles/styled-edit-form add-form)])

(defn add []
  (styles/styled-layout add-page))

(defn- edit-form [{:keys [classes] :as props}]
  (let [role (subscribe [:roles/edit])
        user (subscribe [:user])
        menus (subscribe [:menus])
        user-id  (:id @user)]
    (let [{:keys [name note]} @role]
      [:> mui/Paper {:class-name (.-paper classes)}
       [:form {:name       "add-role-form"
               :class-name (.-root classes)}
        [:> mui/TextField {:name       "name"
                           :label      "名称"
                           :size       "small"
                           :required   true
                           :full-width true
                           :default-value      name
                           :on-change  #(let [value (-> % .-target .-value)]
                                          (dispatch [:roles/set-attr :name value]))}]
        [:> mui/TextField {:name       "note"
                           :label      "备注"
                           :size       "small"
                           :default-value      note
                           :full-width true
                           :on-change  #(let [value (-> % .-target .-value)]
                                          (dispatch [:roles/set-attr :note value]))}]
        [:> mui/Divider]
        [:> mui/Typography "菜单列表"]
        [:> TreeView {:default-collapse-icon mui-icons/ExpandMore
                      :default-expand-icon   mui-icons/ChevronRight}
         (menu-tree-items {:checked-ids (:menus-ids @role)
                           :menus       (:children (utils/make-tree @menus))})]

        [:div {:style      {:margin "normal"}
               :class-name (.-buttons classes)}
         [:> mui/Button {:type     "button"
                         :variant  "outlined"
                         :size     "small"
                         :color    "primary"
                         :on-click #(do
                                      (dispatch [:menus/set-attr :update_by (:id @user)])
                                      (dispatch [:menus/update @role]))}
          "保存"]
         [:> mui/Button {:type     "button"
                         :variant  "outlined"
                         :size     "small"
                         :color    "secondary"
                         :on-click #(js/history.go -1)}
          "返回"]]]])))

(defn- edit-page [props]
  [c/layout props
   (styles/styled-edit-form edit-form)])

(defn edit []
  (styles/styled-layout edit-page))

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

(defn query-form [{:keys [classes]}]
  (let [query-params (subscribe [:roles/query-params])]
    (fn []
      [:> mui/Paper {:class-name (.-paper classes)}
       [:form {:name       "query-form"
               :class-name (.-root classes)
               :size "small"}
        [:div
         [:> mui/TextField {:name      "name"
                            :label     "名称"
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
                         :on-click #(navigate! "/roles/add")}
          "新增"]]]])))

(defn list-table [{:keys [classes]}]
  (let [roles (subscribe [:roles])
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
                [:> mui/IconButton {:color    "primary"
                                    :size     "small"
                                    :on-click #(navigate! (str "/roles/" id "/edit"))}
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
   [:div
    (styles/styled-edit-form delete-dialog)
    (styles/styled-form query-form)
    (styles/styled-table list-table)]])

(defn home []
  (styles/styled-layout query-page))
