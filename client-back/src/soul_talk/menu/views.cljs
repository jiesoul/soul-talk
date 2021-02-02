(ns soul-talk.menu.views
  (:require [soul-talk.common.views :as c]
            [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [soul-talk.routes :refer [navigate!]]
            ["semantic-ui-react" :refer [Form Button Table Divider Icon Container Card]]
            [soul-talk.utils :as du]))

(defn- add-form []
  (let [user    (subscribe [:user])
        menu (r/atom {:create_by (:id @user)
                      :update_by (:id @user)})
        id (r/cursor menu [:id])
        name (r/cursor menu [:name])
        url (r/cursor menu [:url])
        pid (r/cursor menu [:pid])
        note (r/cursor menu [:note])]
    [:> Form {:name "add-menu-form"}
     [:> Form.Input {:name       "id"
                     :label      "id"
                     :size       "small"
                     :required   true
                     :on-change  #(let [value (-> % .-target .-value)]
                                    (reset! id (js/parseInt value)))}]
     [:> Form.Input {:name       "name"
                     :label      "名称"
                     :size       "small"
                     :required   true
                     :on-change  #(let [value (-> % .-target .-value)]
                                    (reset! name value))}]
     [:> Form.Input {:name       "url"
                     :label      "地址"
                     :size       "small"
                     :on-change  #(let [value (-> % .-target .-value)]
                                    (reset! url value))}]
     [:> Form.Input {:name       "pid"
                     :label      "父id"
                     :size       "small"
                     :required   true
                     :on-change  #(let [value (-> % .-target .-value)]
                                    (reset! pid (js/parseInt value)))}]
     [:> Form.Input {:name       "note"
                     :label      "备注"
                     :size       "small"
                     :on-change  #(let [value (-> % .-target .-value)]
                                    (reset! note value))}]
     [:div {:style {:text-align "center"}}
      [:> Button {
                  :size     "small"
                  :color    "primary"
                  :on-click #(let [user-id (:id @user)]
                               (dispatch [:menus/set-attr :update_by user-id :create_by user-id])
                               (dispatch [:menus/add @menu]))}
       "保存"]
      [:> Button {
                  :size     "small"
                  :color    "secondary"
                  :on-click #(navigate! (str "/menus"))}
       "返回"]]]))

(defn add []
  [c/layout [add-form]])


(defn- edit-form []
  (let [menu (subscribe [:menus/edit])
        user (subscribe [:user])]
    (if @menu
      (let [{:keys [id name url pid note]} @menu]
        [:> Form {:name       "edit-menu-form"}
         [:> Form.Input {:name          "id"
                         :label         "id"
                         :size          "small"
                         :required      true
                         :default-value id
                         :on-change     #(dispatch [:menus/set-attr :id (-> % .-target .-value js/parseInt)])}]
         [:> Form.Input {:name          "name"
                         :label         "名称"
                         :size          "small"
                         :required      true
                         :default-value name
                         :on-change     #(let [value (-> % .-target .-value)]
                                           (dispatch [:menus/set-attr :name value]))}]
         [:> Form.Input {:name          "url"
                         :label         "地址"
                         :size          "small"
                         :default-value url
                         :on-change     #(let [value (-> % .-target .-value)]
                                           (dispatch [:menus/set-attr :url value]))}]
         [:> Form.Input {:name          "pid"
                         :label         "父id"
                         :size          "small"
                         :required      true
                         :default-value pid
                         :on-change     #(let [value (-> % .-target .-value)]
                                           (dispatch [:menus/set-attr :pid (js/parseInt value)]))}]
         [:> Form.Input {:name          "note"
                         :label         "备注"
                         :size          "small"
                         :default-value note
                         :on-change     #(let [value (-> % .-target .-value)]
                                           (dispatch [:menus/set-attr :note value]))}]
         [:div {:style      {:margin "normal"
                             :text-align "center"}}
          [:> Button {
                          :size     "small"
                          :color    "green"
                          :on-click #(do
                                       (dispatch [:menus/set-attr :update_by (:id @user)])
                                       (dispatch [:menus/update @menu]))}
           "保存"]
          [:> Button {
                          :size     "small"
                          :on-click #(navigate! (str "/menus"))}
           "返回"]]
         ]))))

(defn edit []
  [c/layout [edit-form]])

(defn- delete-modal []
  (let [menu (subscribe [:menus/edit])
        delete-status (subscribe [:menus/delete-status])]
    (if @menu
      (let [{:keys [id name]} @menu]
        ^{:key "delete-menu-dialog"}
        [c/modal {:open      @delete-status
                  :title    "删除菜单"
                  :ok-text  "确认"
                  :on-close #(dispatch [:menus/set-delete-status false])
                  :on-ok    #(dispatch [:menus/delete id])}
         (str "你确定要删除" name "吗？")]))))

(defn- query-form []
  (let [query-params (subscribe [:menus/query-params])]
    [:> Form {:name "query-form"
              :size "mini"}
     [:> Form.Group
      [:> Form.Input {:name      "id"
                      :label     "id"
                      :inline true
                      :on-change #(dispatch [:menus/set-query-params :id (-> % .-target .-value)])}]
      [:> Form.Input {:name      "name"
                      :label     "name"
                      :inline true
                      :on-change #(dispatch [:menus/set-query-params :name (-> % .-target .-value)])}]
      [:> Form.Input {:name      "pid"
                      :label     "父id"
                      :inline true
                      :on-change #(dispatch [:menus/set-query-params :pid (-> % .-target .-value)])}]]
     [:div {:style {:text-align "right"}}
      [:> Button {:size     "mini"
                  :basic    true
                  :icon "search"
                  :content "查询"
                  :on-click #(dispatch [:menus/load-page @query-params])}]
      [:> Button {:basic    true
                  :size     "mini"
                  :color    "green"
                  :icon "add"
                  :content "新增"
                  :on-click (fn []
                              (navigate! (str "/menus/add")))}]]]))

(defn list-table []
  (let [menus (subscribe [:menus])
        pagination (subscribe [:menus/pagination])
        query-params (subscribe [:menus/query-params])]
    (fn []
      (let [{:keys [per_page page total offset]} @pagination]
        [:div
         [:> Table {:celled true
                    :selectable true
                    :size "small"
                    :text-align "center"}
          [:> Table.Header
           [:> Table.Row
            [:> Table.HeaderCell  "序号"]
            [:> Table.HeaderCell  "ID"]
            [:> Table.HeaderCell  "名称"]
            [:> Table.HeaderCell  "地址"]
            [:> Table.HeaderCell  "PID"]
            [:> Table.HeaderCell  "创建时间"]
            [:> Table.HeaderCell  "更新时间"]
            [:> Table.HeaderCell  "备注"]
            [:> Table.HeaderCell  "操作"]
            ]]
          [:> Table.Body
           (doall
             (for [{:keys [index id name pid url note create_at update_at] :as menu} (map #(assoc %1 :index %2) @menus (range offset (+ offset per_page)))]
               ^{:key menu}
               [:> Table.Row {:tab-index index}
                [:> Table.Cell  (inc index)]
                [:> Table.Cell  id]
                [:> Table.Cell  name]
                [:> Table.Cell  url]
                [:> Table.Cell  pid]
                [:> Table.Cell  (du/to-date-time create_at)]
                [:> Table.Cell  (du/to-date-time update_at)]
                [:> Table.Cell  note]
                [:> Table.Cell
                 [:div
                  [:> Button {:color "green"
                              :size     "mini"
                              :icon "edit"
                              :basic true
                              :content "编辑"
                              :on-click #(navigate! (str "/menus/" id "/edit"))}]

                  [:> Button {:color    "red"
                              :icon "delete"
                              :content "删除"
                              :basic true
                                      :size     "mini"
                                      :on-click (fn []
                                                  (do
                                                    (dispatch [:menus/set-attr :id id :name name])
                                                    (dispatch [:menus/set-delete-status true])))}]]]]))]]
         (if @menus
           [:div {:text-align "right"}
            [c/table-page :menus/load-page (merge @query-params @pagination)]])]))))

(defn home []
  [c/layout
   [:<>
    [delete-modal]
    [query-form]
    [:> Divider]
    [list-table]]])