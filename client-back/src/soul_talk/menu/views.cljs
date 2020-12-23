(ns soul-talk.menu.views
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

(defn menu-form []

  )

(defn add-form [{:keys [classes]}]
  (let [menu    (subscribe [:menu])
        user    (subscribe [:user])
        user-id (:id @user)]
    [c/dialog {:open     @*add-visible*
               :title    "添加菜单"
               :on-close (fn [e props]
                           (do
                             (dispatch [:menus/clean-menu])
                             (reset! *add-visible* false)))
               :on-ok    #(let [menu @menu]
                            (assoc menu :update_by user-id)
                            (dispatch [:menus/add (assoc menu :create_by user-id)]))}
     [:form {:name "add-menu-form"
             :class-name (.-root classes)}
      [:> mui/TextField {:name      "id"
                         :label     "id"
                         :size "small"
                         :full-width true
                         :required true
                         :rules     [{:required true}]
                         :on-change #(let [value (-> % .-target .-value)]
                                       (dispatch [:menus/set-attr :id value]))}]
      [:> mui/TextField {:name      "name"
                         :label     "名称"
                         :size "small"
                         :required true
                         :full-width true
                         :rules     [{:required true}]
                         :on-change #(let [value (-> % .-target .-value)]
                                       (dispatch [:menus/set-attr :name value]))}]
      [:> mui/TextField {:name      "url"
                         :label     "地址"
                         :size "small"
                         :full-width true
                         :on-change #(let [value (-> % .-target .-value)]
                                       (dispatch [:menus/set-attr :name value]))}]
      [:> mui/TextField {:name      "pid"
                         :label     "父id"
                         :size "small"
                         :required true
                         :full-width true
                         :rules     [{:required true}]
                         :on-change #(let [value (-> % .-target .-value)]
                                       (dispatch [:menus/set-attr :pid value]))}]
      [:> mui/TextField {:name      "note"
                         :label     "备注"
                         :size "small"
                         :full-width true
                         :on-change #(let [value (-> % .-target .-value)]
                                       (dispatch [:menus/set-attr :note value]))}]]
     ]))

(def ^:dynamic *edit-visible* (r/atom false))
(defn edit-form [{:keys [classes]}]
  (let [menu (subscribe [:menu])
        user (subscribe [:user])
        user-id  (:id @user)]
    (if @menu
      (let [{:keys [id name url pid note]} @menu]
        [c/dialog {:open     @*edit-visible*
                   :title    "编辑数据字典"
                   :on-close #(reset! *edit-visible* false)
                   :on-ok    #(let [menu @menu]
                                (assoc menu :update_by user-id)
                                (dispatch [:menus/update menu]))}
         [:form {:name "add-menu-form"
                 :class-name (.-root classes)}
          [:> mui/TextField {:name  "id"
                             :label "id"
                             :size "small"
                             :full-width true
                             :required true
                             :value id
                             }]
          [:> mui/TextField {:name      "name"
                             :label     "名称"
                             :size "small"
                             :required true
                             :full-width true
                             :value name
                             :on-change #(let [value (-> % .-target .-value)]
                                           (dispatch [:menus/set-attr :name value]))}]
          [:> mui/TextField {:name      "url"
                             :label     "地址"
                             :size "small"
                             :full-width true
                             :value url
                             :on-change #(let [value (-> % .-target .-value)]
                                           (dispatch [:menus/set-attr :name value]))}]
          [:> mui/TextField {:name      "pid"
                             :label     "父id"
                             :size "small"
                             :required true
                             :full-width true
                             :value pid
                             :on-change #(let [value (-> % .-target .-value)]
                                           (dispatch [:menus/set-attr :pid value]))}]
          [:> mui/TextField {:name      "note"
                             :label     "备注"
                             :size "small"
                             :value note
                             :full-width true
                             :on-change #(let [value (-> % .-target .-value)]
                                           (dispatch [:menus/set-attr :note value]))}]
          ]]))))

(def ^:dynamic *delete-dialog-open* (r/atom false))
(def ^:dynamic *delete-id* (r/atom 0))
(defn delete-dialog []
  [c/dialog {:open     @*delete-dialog-open*
             :title    "删除数据字典"
             :ok-text  "确认"
             :on-close #(reset! *delete-dialog-open* false)
             :on-ok    #(do (reset! *delete-dialog-open* false)
                            (dispatch [:data-dices/delete @*delete-id*]))}
   [:> mui/DialogContentText "你确定要删除吗？"]])

(defn query-form [{:keys [classes]}]
  (let [query-params (subscribe [:menus/query-params])
        {:keys [id name pid]} @query-params]
    (fn []
      [:> mui/Paper {:class-name (.-paper classes)}
       [:form {:name       "query-form"
               :class-name (.-root classes)
               :size "small"}
        [:div
         [:> mui/TextField {:name      "id"
                            :label     "id"
                            :value id
                            :size "small"
                            :on-change #(dispatch [:menus/set-query-params :id (-> % .-target .-value)])}]
         [:> mui/TextField {:name      "name"
                            :label     "name"
                            :value name
                            :size "small"
                            :on-change #(dispatch [:menus/set-query-params :name (-> % .-target .-value)])}]
         [:> mui/TextField {:name      "pid"
                            :label     "父id"
                            :value pid
                            :size "small"
                            :on-change #(dispatch [:menus/set-query-params :pid (-> % .-target .-value)])}]]
        [:div {:class-name (.-buttons classes)}
         [:> mui/Button {:color    "primary"
                         :size     "small"
                         :variant  "outlined"
                         :type "reset"
                         :on-click #(dispatch [:menus/clean-query-params])}
          "重置"]
         [:> mui/Button {:variant  "outlined"
                         :size     "small"
                         :color    "primary"
                         :on-click #(dispatch [:menus/load-page @query-params])}
          "搜索"]
         [:> mui/Button {:color    "secondary"
                         :size     "small"
                         :variant  "outlined"
                         :on-click (fn []
                                     (dispatch [:menus/clean-menu])
                                     (reset! *add-visible* true))}
          "新增"]]]])))

(defn list-table [{:keys [classes]}]
  (let [menus (subscribe [:menus])
        pagination (subscribe [:pagination])]
    (fn []
      (let [{:keys [per_page page total total_pages]} @pagination]
        [:> mui/Paper
         [:> mui/TableContainer {:class-name (.-paper classes)
                                 :component  mui/Paper}
          [:> mui/Table {:class-name    (.-table classes)
                         :sticky-header true
                         :aria-label    "list-table"
                         :size          "small"}
           [:> mui/TableHead {:class-name (.-head classes)}
            [:> mui/TableRow {:class-name (.-head classes)}
             [:> mui/TableCell {:align "center"} "ID"]
             [:> mui/TableCell {:align "center"} "名称"]
             [:> mui/TableCell {:align "center"} "地址"]
             [:> mui/TableCell {:align "center"} "PID"]
             [:> mui/TableCell {:align "center"} "备注"]
             [:> mui/TableCell {:align "center"} "操作"]
             ]]
           [:> mui/TableBody {:class-name (.-body classes)}
            (doall
              (for [{:keys [id name pid url note] :as menu} @menus]
                ^{:key menu}
                [:> mui/TableRow {:class-name (.-row classes)}
                 [:> mui/TableCell {:align "center"} id]
                 [:> mui/TableCell {:align "center"} name]
                 [:> mui/TableCell {:align "center"} url]
                 [:> mui/TableCell {:align "center"} pid]
                 [:> mui/TableCell {:align "center"} note]
                 [:> mui/TableCell {:align "center"}
                  [:div
                   [:> mui/IconButton {:color    "primary"
                                       :size     "small"
                                       :on-click (fn []
                                                   (dispatch [:menus/load-menu id])
                                                   (reset! *edit-visible* true))}
                    [:> mui-icons/Edit]]

                   [:> mui/IconButton {:color    "secondary"
                                       :size     "small"
                                       :style    {:margin "0 8px"}
                                       :on-click (fn []
                                                   (do
                                                     (reset! *delete-dialog-open* true)
                                                     (reset! *delete-id* id)))}
                    [:> mui-icons/Delete]]]]
                 ]))]]
          (if @menus
            [c/table-page {:count                   total
                           :rows-per-page           per_page
                           :page                    (dec page)
                           :on-change-page          #()
                           :on-change-rows-per-page #()}])]]))))

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