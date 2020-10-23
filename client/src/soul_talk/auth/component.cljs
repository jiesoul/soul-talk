(ns soul-talk.auth.component
  (:require [reagent.core :as r]
            [re-frame.core :refer [dispatch subscribe]]
            [soul-talk.routes :refer [navigate!]]
            [soul-talk.user.layout :refer [user-layout]]
            [soul-talk.common.component :refer [logo]]
            [antd :as antd]
            ["@ant-design/icons" :as antd-icons])
  (:import goog.History))

(def layout {:labelCol {:span 8} :tailLayout {:span 16}})
(def tailLayout {:wrapperCol {:offset 8 :span 16}})

(defn login-page []
  (r/with-let [login-data (r/atom {:email    ""
                                   :password ""})
               error      (subscribe [:error])
               email      (r/cursor login-data [:email])
               password   (r/cursor login-data [:password])]
    (fn []
      [user-layout
       [:> antd/Row {:align   "middle"
                     :justify "center"}
        [:> antd/Col {:span 4}
         [:> antd/Form {:name "login"
                        :initialValues {:remember true}
                        :style {:textAlign "center"}}
          [:div
           [logo]
           [:h1.h3.mb-3.font-weight-normal.text-center " Login"]]
          [:> antd/Form.Item {:label "Email" :name "email" :rules [{:required true
                                                                    :message "请输入Email"}]}
           [:> antd/Input
            {:id          "email"
             :prefix      (r/as-element [:> antd-icons/UserOutlined])
             :type        :text
             :name        "email"
             :placeholder "请输入Email"
             :required    true
             :auto-focus  true
             :on-change   #(reset! email (-> % .-target .-value))}]]
          [:> antd/Form.Item {:label "Password" :name "Password" :rules [{:required true
                                                                          :message "请输入密码"}]}
           [:> antd/Input.Password
            {:id          "password"
             :prefix      (r/as-element [:> antd-icons/LockOutlined])
             :name        "password"
             :placeholder "请输入密码"
             :required    true
             :on-change   #(reset! password (-> % .-target .-value))}]]
          (when @error
            [:div @error])
          [:div
           [:> antd/Button
            {:type     "primary"
             :block    true
             :on-click #(dispatch [:login @login-data])}
            "Login"]]]]]])))

(defn register-page []
  (r/with-let
    [reg-data (r/atom nil)
     error (subscribe [:error])
     email (r/cursor reg-data [:email])
     password (r/cursor reg-data [:password])
     pass-confirm (r/cursor reg-data [:pass-confirm])]
    (fn []
      [:div.text-center.container
       [:div.form-signin
        [:h1.h3.mb-3.font-weight-normal.text-center "Register"]
        [:label.sr-only
         {:for "email"}]
        [:input#email.form-control
         {:type        :text
          :placeholder "请输入Email"
          :required    true
          :auto-focus  true
          :on-change   #(reset! email (-> % .-target .-value))}]
        [:label.sr-only
         {:for "password"}]
        [:input#password.form-control
         {:type        :password
          :placeholder "请输入密码"
          :required    true
          :on-change   #(reset! password (-> % .-target .-value))}]
        [:label.sr-only
         {:for "pass-confirm"}]
        [:input#pass-confirm.form-control
         {:type        :password
          :placeholder "请再次输入密码"
          :required    true
          :on-change   #(reset! pass-confirm (-> % .-target .-value))}]
        (when @error
          [:div.alert.alert-danger @error])
        [:button.btn.btn-lg.btn-primary.btn-block
         {:type     "submit"
          :on-click #(dispatch [:register @reg-data])}
         "Register"]]])))