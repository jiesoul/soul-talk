(ns soul-talk.auth.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [dispatch subscribe]]
            [soul-talk.routes :refer [navigate!]]
            ["semantic-ui-react" :as sui :refer [Grid Image]]
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



