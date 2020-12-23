(ns soul-talk.role.views
  (:require [soul-talk.common.views :as c]
            [antd :as antd :refer [Row Col Form Input Button Divider Table Modal]]
            ["@ant-design/icons" :as antd-icons :refer [EditOutlined DeleteOutlined]]
            [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [soul-talk.utils :as utils]
            [taoensso.timbre :as log]
            [soul-talk.common.styles :as styles]
            ["@material-ui/core" :as mui]
            ["@material-ui/icons" :as mui-icons]))


(def ^:dynamic *add-visible* (r/atom false))

(defn add-form [{:keys [classes]}]
  (let [role    (subscribe [:role])
        user    (subscribe [:user])
        user-id (:id @user)]
    [c/dialog {:open     @*add-visible*
               :title    "添加菜单"
               :on-close (fn [e props]
                           (do
                             (dispatch [:roles/clean-role])
                             (reset! *add-visible* false)))
               :on-ok    #(let [role @role]
                            (assoc role :update_by user-id)
                            (dispatch [:roles/add role]))}
     [:form {:name "add-role-form"
             :class-name (.-root classes)}
      [:> mui/TextField {:name      "name"
                         :label     "名称"
                         :size "small"
                         :required true
                         :full-width true
                         :rules     [{:required true}]
                         :on-change #(let [value (-> % .-target .-value)]
                                       (dispatch [:roles/set-attr :name value]))}]
      [:> mui/TextField {:name      "note"
                         :label     "备注"
                         :size "small"
                         :full-width true
                         :on-change #(let [value (-> % .-target .-value)]
                                       (dispatch [:roles/set-attr :note value]))}]]]))

(def ^:dynamic *edit-visible* (r/atom false))
(defn edit-form [{:keys [classes]}]
  (let [role (subscribe [:role])
        user (subscribe [:user])
        user-id  (:id @user)]
    (if @role
      (let [{:keys [name note]} @role]
        [c/dialog {:open     @*edit-visible*
                   :title    "编辑角色"
                   :on-close #(reset! *edit-visible* false)
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

(def ^:dynamic *delete-dialog-open* (r/atom false))
(def ^:dynamic *delete-id* (r/atom 0))
(defn delete-dialog []
  [c/dialog {:open     @*delete-dialog-open*
             :title    "删除角色"
             :ok-text  "确认"
             :on-close #(reset! *delete-dialog-open* false)
             :on-ok    #(do (reset! *delete-dialog-open* false)
                            (dispatch [:data-dices/delete @*delete-id*]))}
   [:> mui/DialogContentText "你确定要删除吗？"]])

(defn query-form [{:keys [classes]}]
  (let [query-params (subscribe [:roles/query-params])
        {:keys [id name pid]} @query-params]
    (fn []
      [:> mui/Paper {:class-name (.-paper classes)}
       [:form {:name       "query-form"
               :class-name (.-root classes)
               :size "small"}
        [:div
         [:> mui/TextField {:name      "name"
                            :label     "名称"
                            :value name
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
                                     (dispatch [:roles/clean-role])
                                     (reset! *add-visible* true))}
          "新增"]]]])))

(defn list-table [{:keys [classes]}]
  (let [roles (subscribe [:roles])
        pagination (subscribe [:pagination])
        query-params (subscribe [:roles/query-params])]
    (fn []
      (let [{:keys [per_page page total]} @pagination]
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
             (for [{:keys [id name pid url note] :as role} @roles]
               ^{:key role}
               [:> mui/TableRow {:class-name (.-row classes)}
                [:> mui/TableCell {:align "center"} name]
                [:> mui/TableCell {:align "center"} note]
                [:> mui/TableCell {:align "center"}
                 [:div
                  [:> mui/IconButton {:color    "primary"
                                      :size     "small"
                                      :on-click (fn []
                                                  (dispatch [:roles/load-role id])
                                                  (reset! *edit-visible* true))}
                   [:> mui-icons/Edit]]

                  [:> mui/IconButton {:color    "secondary"
                                      :size     "small"
                                      :style    {:margin "0 8px"}
                                      :on-click (fn []
                                                  (do
                                                    (reset! *delete-dialog-open* true)
                                                    (reset! *delete-id* id)))}
                   [:> mui-icons/Delete]]]]]))]]
         (if @roles
           [c/table-page :roles/load-page (merge @query-params @pagination) ])]))))

(defn query-page
  [props]
  [c/layout props
   [:<>
    (styles/styled-edit-form add-form)
    (styles/styled-edit-form edit-form)
    (styles/styled-edit-form delete-dialog)
    (styles/styled-form query-form)
    (styles/styled-table list-table)
    ]])

(defn home []
  (styles/main query-page))
