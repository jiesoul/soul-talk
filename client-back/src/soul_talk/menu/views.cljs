(ns soul-talk.menu.views
  (:require [soul-talk.common.views :as c]
            [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [soul-talk.routes :refer [navigate!]]
            ["semantic-ui-react" :refer [Form Button Table Divider Icon Container Card]]
            [soul-talk.utils :as du]))

(defn- new-form []
  (let [user    (subscribe [:user])
        menu (subscribe [:menu/edit])
        _ (dispatch [:menu/set-attr {:create_by (:id @user)
                                      :update_by (:id @user)}])]
    [:> Form {:name "add-menu-form"}
     [:> Form.Input {:name      "id"
                     :label     "id"
                     :required  true
                     :on-change #(let [value (-> % .-target .-value)]
                                   (dispatch [:menu/set-attr {:id (js/parseInt value)}]))}]
     [:> Form.Input {:name      "name"
                     :label     "名称"
                     :required  true
                     :on-change #(let [value (-> % .-target .-value)]
                                   (dispatch [:menu/set-attr {:name value}]))}]
     [:> Form.Input {:name      "url"
                     :label     "地址"
                     :on-change #(let [value (-> % .-target .-value)]
                                   (dispatch [:menu/set-attr {:url value}]))}]
     [:> Form.Input {:name      "pid"
                     :label     "父id"
                     :required  true
                     :on-change #(let [value (-> % .-target .-value)]
                                   (dispatch [:menu/set-attr {:pid (js/parseInt value)}]))}]
     [:> Form.Input {:name      "note"
                     :label     "备注"
                     :on-change #(let [value (-> % .-target .-value)]
                                   (dispatch [:menu/set-attr {:note value}]))}]
     [:div.button-center
      [:> Button {:content  "返回"
                  :on-click #(navigate! (str "/menu"))}]
      [:> Button {:icon     "save"
                  :content  "保存"
                  :positive true
                  :on-click #(dispatch [:menu/save @menu])}]]]))

(defn new []
  [c/layout [new-form]])


(defn- edit-form []
  (let [menu (subscribe [:menu/edit])
        user (subscribe [:user])
        _ (dispatch [:menu/set-attr {:update_by (:id @user)}])]
    (if @menu
      (let [{:keys [id name url pid note]} @menu]
        [:> Form {:name       "edit-menu-form"}
         [:> Form.Input {:name          "id"
                         :label         "id"
                         :required      true
                         :default-value id
                         :on-change     #(dispatch [:menu/set-attr {:id (-> % .-target .-value js/parseInt)}])}]
         [:> Form.Input {:name          "name"
                         :label         "名称"
                         :required      true
                         :default-value name
                         :on-change     #(let [value (-> % .-target .-value)]
                                           (dispatch [:menu/set-attr {:name value}]))}]
         [:> Form.Input {:name          "url"
                         :label         "地址"
                         :default-value url
                         :on-change     #(let [value (-> % .-target .-value)]
                                           (dispatch [:menu/set-attr {:url value}]))}]
         [:> Form.Input {:name          "pid"
                         :label         "父id"
                         :required      true
                         :default-value pid
                         :on-change     #(let [value (-> % .-target .-value)]
                                           (dispatch [:menu/set-attr {:pid (js/parseInt value)}]))}]
         [:> Form.Input {:name          "note"
                         :label         "备注"
                         :default-value note
                         :on-change     #(let [value (-> % .-target .-value)]
                                           (dispatch [:menu/set-attr {:note value}]))}]
         [:div.button-center
          [:> Button {:on-click #(navigate! (str "/menu"))}
           "返回"]
          [:> Button {:positive true
                      :on-click #(dispatch [:menu/update @menu])}
           "保存"]]]))))

(defn edit []
  [c/layout [edit-form]])

(defn- delete-modal []
  (let [menu (subscribe [:menu/edit])
        delete-dialog (subscribe [:menu/delete-dialog])]
    (if @menu
      (let [{:keys [id name]} @menu]
        ^{:key "delete-menu-dialog"}
        [c/confirm {:open   @delete-dialog
                  :title    "删除菜单"
                  :ok-text  "确认"
                  :on-close #(dispatch [:menu/set-delete-dialog false])
                  :on-ok    #(dispatch [:menu/delete id])}
         (str "你确定要删除" name "吗？")]))))

(defn- query-form []
  (let [query-params (subscribe [:menu/query-params])]
    [:> Form {:name "query-form"}
     [:> Form.Group
      [:> Form.Input {:name      "id"
                      :label     "id"
                      :inline true
                      :on-change #(dispatch [:menu/set-query-params :id (-> % .-target .-value)])}]
      [:> Form.Input {:name      "name"
                      :label     "name"
                      :inline true
                      :on-change #(dispatch [:menu/set-query-params :name (-> % .-target .-value)])}]
      [:> Form.Input {:name      "pid"
                      :label     "父id"
                      :inline true
                      :on-change #(dispatch [:menu/set-query-params :pid (-> % .-target .-value)])}]]
     [:div {:style {:text-align "center"}}
      [:> Button {:icon     "search"
                  :content  "查询"
                  :on-click #(dispatch [:menu/load-page @query-params])}]
      [:> Button {:color    "green"
                  :icon     "add"
                  :content  "新增"
                  :on-click (fn []
                              (navigate! (str "/menu/new")))}]]]))

(defn list-table []
  (let [menus (subscribe [:menu/list])
        pagination (subscribe [:menu/pagination])
        query-params (subscribe [:menu/query-params])]
    (let [{:keys [per_page page total offset]} @pagination]
      [:div
       [:> Table {:celled     true
                  :selectable true
                  :text-align "center"}
        [:> Table.Header
         [:> Table.Row
          [:> Table.HeaderCell "序号"]
          [:> Table.HeaderCell "ID"]
          [:> Table.HeaderCell "名称"]
          [:> Table.HeaderCell "地址"]
          [:> Table.HeaderCell "PID"]
          [:> Table.HeaderCell "创建时间"]
          [:> Table.HeaderCell "更新时间"]
          [:> Table.HeaderCell "备注"]
          [:> Table.HeaderCell "操作"]
          ]]
        [:> Table.Body
         (doall
           (for [{:keys [index id name pid url note create_at update_at] :as menu} (map #(assoc %1 :index %2) @menus (range offset (+ offset per_page)))]
             ^{:key menu}
             [:> Table.Row {:tab-index index}
              [:> Table.Cell (inc index)]
              [:> Table.Cell id]
              [:> Table.Cell name]
              [:> Table.Cell url]
              [:> Table.Cell pid]
              [:> Table.Cell (du/to-date-time create_at)]
              [:> Table.Cell (du/to-date-time update_at)]
              [:> Table.Cell note]
              [:> Table.Cell
               [:div
                [:> Button {:positive true
                            :icon "edit"
                            :on-click #(navigate! (str "/menu/" id "/edit"))}]
                [:> Button {:negative true
                            :icon "delete"
                            :on-click (fn []
                                        (do
                                          (dispatch [:menu/set-attr {:id id :name name}])
                                          (dispatch [:menu/set-delete-dialog true])))}]]]]))]]
       (if @menus
         [c/table-page :menu/load-page (merge @query-params @pagination)])
       ])))

(defn home []
  [c/layout
   [:<>
    [delete-modal]
    [query-form]
    [:> Divider]
    [list-table]]])