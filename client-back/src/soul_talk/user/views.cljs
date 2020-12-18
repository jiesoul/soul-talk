(ns soul-talk.user.views
  (:require [soul-talk.common.views :refer [manager-layout]]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [antd :as antd]
            [soul-talk.common.styles :as styles]))

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
        [manager-layout
         [:> antd/Layout.Content {:className "main" :align "center"}
          [:> antd/Row
           [:> antd/Col {:span 8 :offset 8}
            [:> antd/Input {:id           "username"
                            :disabled     true
                            :addon-before "用户名："
                            :value        (:name @pass-data)}]
            [:> antd/Input.Password {:id           "old-pass"
                                     :name         "old-pass"
                                     :placeholder  "请输入旧密码"
                                     :addon-before "旧密码："
                                     :on-change    #(reset! pass-old (.-target.value %))}]
            [:> antd/Input.Password {:id           "pass-new"
                                     :name         "pass-new"
                                     :placeholder  "请输入新密码"
                                     :addon-before "新密码："
                                     :on-change    #(reset! pass-new (.-target.value %))}]
            [:> antd/Input.Password {:id           "pass-confirm"
                                     :name         "pass-confirm"
                                     :placeholder  "重复新密码"
                                     :addon-before "新密码："
                                     :on-change    #(reset! pass-confirm (.-target.value %))}]
            [:div
             [:> antd/Button {:type     "primary"
                              :on-click #(rf/dispatch [:change-pass @pass-data])}
              "保存"]]]]]]))))


(defn user-profile-page []
  (r/with-let [user (rf/subscribe [:user])
               edited-user (r/atom @user)
               name (r/cursor edited-user [:name])
               error (rf/subscribe [:error])]
    (fn []
      (if @user
        [manager-layout
         [:> antd/Layout.Content
          [:> antd/Row
           [:> antd/Col {:span 8 :offset 8}
            [:> antd/Input
             {:id           "email"
              :addon-before "邮箱："
              :disabled     true
              :value        (:email @edited-user)
              :read-only    true}
             ]
            [:> antd/Input
             {:id           "name"
              :addon-before "名字："
              :defaultValue @name
              :on-change    #(let [val (-> % .-target .-value)]
                               (reset! name val))}]
            (when @error
              [:div.alert.alert-message @error])
            [:div {:style {:text-align "center"}}
             [:> antd/Button
              {:type     :submit
               :on-click #(rf/dispatch [:save-user-profile @edited-user])}
              "保存"]]]]]]))))


(defn copyright []
  [:> antd/Layout.Footer
   "Copyright "
   [:> antd/Icon]
   " 2019 "])

(defn user-layout [children]
  [:> antd/Layout {:title ""}
   [:> antd/Layout.Content {:style {:min-height "100vh"
                                    :padding    "24px 0 20px 0"}}
    children]
   copyright])

(defn home []
  (styles/main user-layout))
