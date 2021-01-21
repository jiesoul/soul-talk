(ns soul-talk.menu.views
  (:require [soul-talk.common.views :as c]
            [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [soul-talk.common.styles :as styles]
            [soul-talk.routes :refer [navigate!]]
            ["@material-ui/core" :as mui]
            ["@material-ui/icons" :as mui-icons]))

(defn- add-form [{:keys [classes]}]
  (let [user    (subscribe [:user])
        menu (r/atom {:create_by (:id @user)
                      :update_by (:id @user)})
        id (r/cursor menu [:id])
        name (r/cursor menu [:name])
        url (r/cursor menu [:url])
        pid (r/cursor menu [:pid])
        note (r/cursor menu [:note])]
    [:> mui/Paper {:class-name (.-paper classes)}
     [:form {:name "add-menu-form"
             :class-name (.-root classes)}
      [:> mui/TextField {:name       "id"
                         :label      "id"
                         :size       "small"
                         :full-width true
                         :required   true
                         :on-change  #(let [value (-> % .-target .-value)]
                                        (reset! id (js/parseInt value)))}]
      [:> mui/TextField {:name       "name"
                         :label      "名称"
                         :size       "small"
                         :required   true
                         :full-width true
                         :rules      [{:required true}]
                         :on-change  #(let [value (-> % .-target .-value)]
                                        (reset! name value))}]
      [:> mui/TextField {:name       "url"
                         :label      "地址"
                         :size       "small"
                         :full-width true
                         :on-change  #(let [value (-> % .-target .-value)]
                                        (reset! url value))}]
      [:> mui/TextField {:name       "pid"
                         :label      "父id"
                         :size       "small"
                         :required   true
                         :full-width true
                         :rules      [{:required true}]
                         :on-change  #(let [value (-> % .-target .-value)]
                                        (reset! pid (js/parseInt value)))}]
      [:> mui/TextField {:name       "note"
                         :label      "备注"
                         :size       "small"
                         :full-width true
                         :on-change  #(let [value (-> % .-target .-value)]
                                        (reset! note value))}]
      [:div {:style      {:margin "normal"}
             :class-name (.-buttons classes)}
       [:> mui/Button {:type     "button"
                       :variant  "outlined"
                       :size     "small"
                       :color    "primary"
                       :on-click #(let [user-id (:id @user)]
                                    (dispatch [:menus/set-attr :update_by user-id :create_by user-id])
                                    (dispatch [:menus/add @menu]))}
        "保存"]
       [:> mui/Button {:type     "button"
                       :variant  "outlined"
                       :size     "small"
                       :color    "secondary"
                       :on-click #(navigate! (str "/menus"))}
        "返回"]]]]))

(defn- add-page [props]
  [c/layout props
   (styles/styled-edit-form add-form)])

(defn add []
  (styles/styled-layout add-page))


(defn- edit-form [{:keys [classes]}]
  (let [menu (subscribe [:menus/edit])
        user (subscribe [:user])]
      (if @menu
        (let [{:keys [id name url pid note]} @menu]
          [:> mui/Paper {:class-name (.-paper classes)}
           [:form {:name       "add-menu-form"
                   :class-name (.-root classes)}
            [:> mui/TextField {:name          "id"
                               :label         "id"
                               :size          "small"
                               :full-width    true
                               :required      true
                               :default-value id
                               :on-change     #(dispatch [:menus/set-attr :id (-> % .-target .-value js/parseInt)])}]
            [:> mui/TextField {:name          "name"
                               :label         "名称"
                               :size          "small"
                               :required      true
                               :full-width    true
                               :default-value name
                               :on-change     #(let [value (-> % .-target .-value)]
                                                 (dispatch [:menus/set-attr :name value]))}]
            [:> mui/TextField {:name          "url"
                               :label         "地址"
                               :size          "small"
                               :full-width    true
                               :default-value url
                               :on-change     #(let [value (-> % .-target .-value)]
                                                 (dispatch [:menus/set-attr :url value]))}]
            [:> mui/TextField {:name          "pid"
                               :label         "父id"
                               :size          "small"
                               :required      true
                               :full-width    true
                               :default-value pid
                               :on-change     #(let [value (-> % .-target .-value)]
                                                 (dispatch [:menus/set-attr :pid (js/parseInt value)]))}]
            [:> mui/TextField {:name          "note"
                               :label         "备注"
                               :size          "small"
                               :default-value note
                               :full-width    true
                               :on-change     #(let [value (-> % .-target .-value)]
                                                 (dispatch [:menus/set-attr :note value]))}]
            [:div {:style      {:margin "normal"}
                   :class-name (.-buttons classes)}
             [:> mui/Button {:type     "button"
                             :variant  "outlined"
                             :size     "small"
                             :color    "primary"
                             :on-click #(do
                                          (dispatch [:menus/set-attr :update_by (:id @user)])
                                          (dispatch [:menus/update @menu]))}
              "保存"]
             [:> mui/Button {:type     "button"
                             :variant  "outlined"
                             :size     "small"
                             :color    "secondary"
                             :on-click #(navigate! (str "/menus"))}
              "返回"]]
            ]]))))

(defn- edit-page [props]
  [c/layout props
   (styles/styled-form edit-form)])

(defn edit []
  (styles/styled-layout edit-page))

(defn- delete-form []
  (let [menu (subscribe [:menus/edit])
        delete-status (subscribe [:menus/delete-status])]
    (println "...menu: " @menu)
    (if @menu
      (let [{:keys [id name]} @menu]
        ^{:key "delete-menu-dialog"}
        [c/dialog {:open     @delete-status
                   :title    "删除菜单"
                   :ok-text  "确认"
                   :on-close #(dispatch [:menus/set-delete-status false])
                   :on-ok    #(dispatch [:menus/delete id])}
         [:> mui/DialogContentText (str "你确定要删除" name "吗？")]]))))

(defn- query-form [{:keys [classes]}]
  (let [query-params (subscribe [:menus/query-params])]
    (fn []
      [:> mui/Paper {:class-name (.-paper classes)}
       [:form {:name       "query-form"
               :class-name (.-root classes)
               :size "small"}
        [:div
         [:> mui/TextField {:name      "id"
                            :label     "id"
                            :size "small"
                            :on-change #(dispatch [:menus/set-query-params :id (-> % .-target .-value)])}]
         [:> mui/TextField {:name      "name"
                            :label     "name"
                            :size "small"
                            :on-change #(dispatch [:menus/set-query-params :name (-> % .-target .-value)])}]
         [:> mui/TextField {:name      "pid"
                            :label     "父id"
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
                                     (navigate! (str "/menus/add")))}
          "新增"]]]])))

(defn list-table [{:keys [classes]}]
  (let [menus (subscribe [:menus])
        pagination (subscribe [:menus/pagination])
        query-params (subscribe [:menus/query-params])]
    (fn []
      (let [{:keys [per_page page total offset]} @pagination]
        [:> mui/TableContainer {:class-name (.-paper classes)
                                :component  mui/Paper}
         [:> mui/Table {:class-name    (.-table classes)
                        :sticky-header true
                        :aria-label    "list-table"
                        :size          "small"}
          [:> mui/TableHead {:class-name (.-head classes)}
           [:> mui/TableRow {:class-name (.-head classes)}
            [:> mui/TableCell {:align "center"} "序号"]
            [:> mui/TableCell {:align "center"} "ID"]
            [:> mui/TableCell {:align "center"} "名称"]
            [:> mui/TableCell {:align "center"} "地址"]
            [:> mui/TableCell {:align "center"} "PID"]
            [:> mui/TableCell {:align "center"} "备注"]
            [:> mui/TableCell {:align "center"} "操作"]
            ]]
          [:> mui/TableBody {:class-name (.-body classes)}
           (doall
             (for [{:keys [index id name pid url note] :as menu} (map #(assoc %1 :index %2) @menus (range offset (+ offset per_page)))]
               ^{:key menu}
               [:> mui/TableRow {:class-name (.-row classes)
                                 :tab-index index}
                [:> mui/TableCell {:align "center"} (inc index)]
                [:> mui/TableCell {:align "center"} id]
                [:> mui/TableCell {:align "center"} name]
                [:> mui/TableCell {:align "center"} url]
                [:> mui/TableCell {:align "center"} pid]
                [:> mui/TableCell {:align "center"} note]
                [:> mui/TableCell {:align "center"}
                 [:div
                  [:> mui/IconButton {:color    "primary"
                                      :size     "small"
                                      :on-click #(navigate! (str "/menus/" id "/edit"))}
                   [:> mui-icons/Edit]]

                  [:> mui/IconButton {:color    "secondary"
                                      :size     "small"
                                      :style    {:margin "0 8px"}
                                      :on-click (fn []
                                                  (do
                                                    (dispatch [:menus/set-attr :id id :name name])
                                                    (dispatch [:menus/set-delete-status true])))}
                   [:> mui-icons/Delete]]]]]))]]
         (if @menus
           [c/table-page :menus/load-page (merge @query-params @pagination) ])]))))

(defn query-page
  [props]
  [c/layout props
   [:<>
    (styles/styled-edit-form delete-form)
    (styles/styled-form query-form)
    (styles/styled-table list-table)
    ]])

(defn home []
  (styles/styled-layout query-page))