(ns soul-talk.pages.users
  (:require [soul-talk.pages.common :as c]
            [reagent.core :as r]
            [soul-talk.auth-validate :refer [change-pass-errors]]
            [taoensso.timbre :as log]
            [re-frame.core :refer [subscribe dispatch]]
            [cljs-time.format :as f]))

(def formatter (f/formatter "yyyyMMdd"))

(defn user-list []
  (r/with-let
    [users @(subscribe [:admin/users])]
    (if users
      [:div.table-responsive
       [:table.table.table-striped.table-sm
        [:thead
         [:tr
          [:th "email"]
          [:th "name"]
          [:th "last_login"]
          [:th "Header"]]]
        [:tbody
         (for [{:keys [email name last_login] :as user} users]
           ^{:key user} [:tr
                         [:td email]
                         [:td name]
                         [:td (str last_login)]
                         [:td "action"]])]]])))

(defn users-page []
  [:div
   [:h2 "User List"]
   [user-list]])

(defn change-pass-page []
  (r/with-let [user @(subscribe [:user])
                pass-data (r/atom {:email  (:email user)})
                error (subscribe [:error])]
    (if user
      [:div.container-fluid
       [:div.form-signin
        [:h1.h3.mb-3.font-weight-normal.text-center "修改密码"]
        [:div
         [:div.well.well-sm "* 为必填项"]
         [c/password-input "旧密码" :pass-old "输入密码最少8位" pass-data]
         [c/password-input "新密码" :pass-new "输入密码最少8位" pass-data]
         [c/password-input "确认密码" :pass-confirm "确认密码和上面一样" pass-data]
         (when @error
           [:div.alert.alert-danger @error])
         [:div
          [:input.btn.btn-primary.btn-block
           {:type     :submit
            :value    "保存"
            :on-click #(dispatch [:change-pass @pass-data])}]]]]])))


(defn user-profile-page []
  (r/with-let [user @(subscribe [:user])
                user-data (r/atom user)
               error (subscribe [:error])]
    (if user
      [:div.container-fluid
       [:div.form-group
        [:h1.h3.mb-3.font-weight-normal.text-center "User Profile"]
        [:div.form-signin
         [:div.form-group
          [:label {:for "email"} "Email"]
          [:input.form-control
           {:id        "email"
            :type      :text
            :value     (:email @user-data)
            :read-only true}
           ]]
         [:div.form-group
          [:label {:for "name"} "Name"]
          [:input.form-control
           {:id        "name"
            :type      :text
            :value     (:name @user-data)
            :on-change #(swap! user-data assoc :name (-> % .-target .-value))}]]
         (when @error
           [:div.alert.alert-message @error])
         [:div.form-group
          [:input.btn.btn-primary.btn-block
           {:type     :submit
            :value    "Save"
            :on-click #(dispatch [:save-user-profile @user-data])}]]]]])))

