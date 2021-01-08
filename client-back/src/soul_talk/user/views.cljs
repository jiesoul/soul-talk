(ns soul-talk.user.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [soul-talk.common.styles :as styles]
            [soul-talk.common.views :as c]
            ["@material-ui/core" :as mui]))

(defn users-page []
  (fn []
    [:div
     [:h2 "User List"]
     (r/with-let
       [users (rf/subscribe [:admin/users])]
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
  (r/with-let [user (rf/subscribe [:user])
               pass-data (r/atom @user)
               pass-old (r/cursor pass-data [:pass-old])
               pass-new (r/cursor pass-data [:pass-new])
               pass-confirm (r/cursor pass-data [:pass-confirm])
               error (rf/subscribe [:error])]
    (fn []
      (if @user
        [c/layout
         [:> mui/Layout.Content {:className "main" :align "center"}
          [:> mui/Row
           [:> mui/Col {:span 8 :offset 8}
            [:> mui/Input {:id           "username"
                            :disabled     true
                            :addon-before "用户名："
                            :value        (:name @pass-data)}]
            [:> mui/Input.Password {:id           "old-pass"
                                     :name         "old-pass"
                                     :placeholder  "请输入旧密码"
                                     :addon-before "旧密码："
                                     :on-change    #(reset! pass-old (.-target.value %))}]
            [:> mui/Input.Password {:id           "pass-new"
                                     :name         "pass-new"
                                     :placeholder  "请输入新密码"
                                     :addon-before "新密码："
                                     :on-change    #(reset! pass-new (.-target.value %))}]
            [:> mui/Input.Password {:id           "pass-confirm"
                                     :name         "pass-confirm"
                                     :placeholder  "重复新密码"
                                     :addon-before "新密码："
                                     :on-change    #(reset! pass-confirm (.-target.value %))}]
            [:div
             [:> mui/Button {:type     "primary"
                              :on-click #(rf/dispatch [:change-pass @pass-data])}
              "保存"]]]]]]))))


(defn user-profile-page [props]
  (r/with-let [user (rf/subscribe [:user])
               edited-user (r/atom @user)
               name (r/cursor edited-user [:name])
               error (rf/subscribe [:error])]
    (fn []
      (if @user
        [c/layout props
         [:> mui/Layout.Content
          [:> mui/Row
           [:> mui/Col {:span 8 :offset 8}
            [:> mui/Input
             {:id           "email"
              :addon-before "邮箱："
              :disabled     true
              :value        (:email @edited-user)
              :read-only    true}
             ]
            [:> mui/Input
             {:id           "name"
              :addon-before "名字："
              :defaultValue @name
              :on-change    #(let [val (-> % .-target .-value)]
                               (reset! name val))}]
            (when @error
              [:div.alert.alert-message @error])
            [:div {:style {:text-align "center"}}
             [:> mui/Button
              {:type     :submit
               :on-click #(rf/dispatch [:save-user-profile @edited-user])}
              "保存"]]]]]]))))


(defn copyright []
  [:> mui/Layout.Footer
   "Copyright "
   [:> mui/Icon]
   " 2019 "])

(defn user-layout [children]
  [:> mui/Layout {:title ""}
   [:> mui/Layout.Content {:style {:min-height "100vh"
                                    :padding    "24px 0 20px 0"}}
    children]
   copyright])

(defn home []
  (styles/main user-layout))
