(ns soul-talk.auth.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [dispatch subscribe]]
            [soul-talk.routes :refer [navigate!]]
            ["semantic-ui-react" :as sui :refer [Grid Image Button Table Form]]
            [soul-talk.common.views :as c])
  (:import goog.History))

(defn login-page []
  (let [login-user (r/atom {:email "" :password ""})
        email (r/cursor login-user [:email])
        password (r/cursor login-user [:password])]
    [:div
     [:> Grid {:text-align "center"
               :style {:height "100vh"
                       :background-image "url(https://source.unsplash.com/random)"
                       :background-repeat "no-repeat"
                       :background-size "cover"}
               :vertical-align "middle"}
      [:> Grid.Column {:style {:max-width "450px"}}
       [:> sui/Form {:size "large"}
        [:> sui/Segment {:stacked true}
         [:> sui/Header {:as         "h2"
                         :color      "teal"
                         :text-align "center"}
          (str "Log in")]
         [:> sui/Form.Input {:fluid         true
                             :margin        "normal"
                             :icon          "user"
                             :icon-position "left"
                             :placeholder   "请输入 Email"
                             :required      true
                             :id            "email"
                             :name          "email"
                             :auto-focus    true
                             :on-change     #(reset! email (-> % .-target .-value))}]
         [:> sui/Form.Input {:fluid         true
                             :margin        "normal"
                             :icon          "lock"
                             :icon-position "left"
                             :placeholder   "请输入密码"
                             :required      true
                             :name          "password"
                             :id            "password"
                             :type          "password"
                             :on-change     #(reset! password (-> % .-target .-value))}]

         [:> sui/Button {:type     "button"
                         :fluid    true
                         :size     "large"
                         :color    "teal"
                         :on-click #(dispatch [:login @login-user])}
          "登录"]
         [:div {:style {:margin-top "20px"}}
          [c/copyright]]]]]]]))


(defn delete-dialog [id]
  (let [open (subscribe [:auth-key/delete-dialog-open])]
    (if @open
      [c/confirm {:open    @open
                  :title    "删除角色"
                  :ok-text  "确认"
                  :on-close #(dispatch [:auth-key/set-delete-dialog-open false])
                  :on-ok    #(do (dispatch [:auth-key/set-delete-dialog-open false])
                                 (dispatch [:auth-key/delete id]))}
       "你确定要删除吗？"])))

(defn query-form []
  (let [query-params (subscribe [:auth-key/query-params])]
    [:> Form {:name       "query-form"
              :size       "small"}
     [:> Form.Group
      [:> Form.Input {:name      "name"
                      :label     "名称"
                      :inline true
                      :on-change #(dispatch [:auth-key/set-query-params :name (-> % .-target .-value)])}]]
     [:div.button-center
      [:> Button {:on-click #(dispatch [:auth-key/load-page @query-params])}
       "搜索"]
      [:> Button {:color    "green"
                  :on-click #(navigate! "/users/new")}
       "新增"]]]))

(defn list-table []
  (let [auth-keys (subscribe [:auth-key/list])
        pagination (subscribe [:auth-key/pagination])
        query-params (subscribe [:auth-key/query-params])]
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
         (for [{:keys [id email name note] :as auth-key} @auth-keys]
           ^{:key auth-key}
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
                                        (dispatch [:auth-key/set-delete-dialog true])
                                        (dispatch [:auth-key/set-attr auth-key])))}]]]]))]]
     (if @auth-keys
       [c/table-page :auth-key/load-page (merge @query-params @pagination)])]))

(defn home []
  [c/layout
   [:<>
    [delete-dialog]
    [query-form]
    [list-table]]])



