(ns soul-talk.user.component
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [soul-talk.common.layout :refer [basic-layout]]
            [soul-talk.user-validate :refer [change-pass-errors]]))

(defn users-page []
  (fn []
    [:div
     [:h2 "User List"]
     (r/with-let
       [users (subscribe [:admin/users])]
       (if @users
         [:div.table-responsive
          [:table.table.table-striped.table-sm
           [:thead
            [:tr
             [:th "email"]
             [:th "name"]
             [:th "last_login"]
             [:th "Header"]]]
           [:tbody
            (for [{:keys [email name last_login] :as user} @users]
              ^{:key user} [:tr
                            [:td email]
                            [:td name]
                            [:td (.toDateString (js/Date. last_login))]
                            [:td "action"]])]]]))]))




(defn change-pass-page []
  (r/with-let [user (subscribe [:user])
               pass-data (r/atom @user)
               pass-old (r/cursor pass-data [:pass-old])
               pass-new (r/cursor pass-data [:pass-new])
               pass-confirm (r/cursor pass-data [:pass-confirm])
               error (subscribe [:error])]
    (fn []
      (if @user
        [basic-layout
         [:> js/antd.Layout.Content {:className "main" :align "center"}
          [:> js/antd.Row
           [:> js/antd.Col {:span 8 :offset 8}
            [:> js/antd.Input {:id           "username"
                               :disabled     true
                               :addon-before "用户名："
                               :value        (:name @pass-data)}]
            [:> js/antd.Input.Password {:id           "old-pass"
                                        :name         "old-pass"
                                        :placeholder  "请输入旧密码"
                                        :addon-before "旧密码："
                                        :on-change    #(reset! pass-old (.-target.value %))}]
            [:> js/antd.Input.Password {:id           "pass-new"
                                        :name         "pass-new"
                                        :placeholder  "请输入新密码"
                                        :addon-before "新密码："
                                        :on-change    #(reset! pass-new (.-target.value %))}]
            [:> js/antd.Input.Password {:id           "pass-confirm"
                                        :name         "pass-confirm"
                                        :placeholder  "重复新密码"
                                        :addon-before "新密码："
                                        :on-change    #(reset! pass-confirm (.-target.value %))}]
            [:div
             [:> js/antd.Button {:type     "primary"
                                 :on-click #(if-let [error (r/as-element (change-pass-errors @pass-data))]
                                              (dispatch [:set-error error])
                                              (dispatch [:change-pass @pass-data]))}
              "保存"]]]]]]))))


(defn user-profile-page []
  (r/with-let [user (subscribe [:user])
               edited-user (r/atom @user)
               name (r/cursor edited-user [:name])
               error (subscribe [:error])]
    (fn []
      (if @user
        [basic-layout
         [:> js/antd.Layout.Content
          [:> js/antd.Row
           [:> js/antd.Col {:span 8 :offset 8}
            [:> js/antd.Input
             {:id           "email"
              :addon-before "邮箱："
              :disabled     true
              :value        (:email @edited-user)
              :read-only    true}
             ]
            [:> js/antd.Input
             {:id           "name"
              :addon-before "名字："
              :defaultValue @name
              :on-change    #(let [val (-> % .-target .-value)]
                               (reset! name val))}]
            (when @error
              [:div.alert.alert-message @error])
            [:div {:style {:text-align "center"}}
             [:> js/antd.Button
              {:type     :submit
               :on-click #(dispatch [:save-user-profile @edited-user])}
              "保存"]]]]]]))))

