(ns soul-talk.auth.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [dispatch subscribe]]
            [soul-talk.routes :refer [navigate!]]
            [soul-talk.common.views :refer [header logo footer]]
            [antd :as antd]
            ["@ant-design/icons" :as antd-icons])
  (:import goog.History))

(defn nav []
  [:> antd/Menu {:className         "home-nav"
                 :mode              "horizontal"}
   [:> antd/Menu.Item "登录"]])

(defn layout [children]
  [:> antd/Layout
   [header nav]
   [:> antd/Layout.Content {:className "site-layout-content"}
    children]
   [footer]])

(defn login-page []
  (r/with-let [login-data (r/atom {:email    ""
                                   :password ""})
               email      (r/cursor login-data [:email])
               password   (r/cursor login-data [:password])]
    (fn []
      [layout
       [:> antd/Row {:align   "middle"
                     :justify "space-around"}
        [:> antd/Col
         [:> antd/Form {:name          "login"
                        :initialValues {:remember true}
                        :style         {:textAlign "center"}
                        :labelCol      {:span 8}
                        :wrapperCol    {:span 16}}
          [:> antd/Form.Item {:label "邮箱" :name "email" :rules [{:required true
                                                                  :message  "请输入Email"}]}
           [:> antd/Input {:id          "email"
                           :prefix      (r/as-element [:> antd-icons/UserOutlined])
                           :type        :text
                           :name        "email"
                           :placeholder "请输入Email"
                           :required    true
                           :auto-focus  true
                           :on-change   #(reset! email (-> % .-target .-value))}]]
          [:> antd/Form.Item {:label "密码" :name "Password" :rules [{:required true
                                                                          :message  "请输入密码"}]}
           [:> antd/Input.Password {:id          "password"
                                    :prefix      (r/as-element [:> antd-icons/LockOutlined])
                                    :name        "password"
                                    :placeholder "请输入密码"
                                    :required    true
                                    :on-change   #(reset! password (-> % .-target .-value))}]]
          [:> antd/Form.Item {:offset 8 :span 16 :align "right"}
           [:> antd/Button {:type     "primary"
                            :htmlType "submit"
                            :on-click #(dispatch [:login @login-data])}
            "登陆"]]]]]])))

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