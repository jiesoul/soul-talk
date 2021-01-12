(ns soul-talk.auth.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [dispatch subscribe]]
            [soul-talk.routes :refer [navigate!]]
            [soul-talk.common.styles :as styles]
            [goog.object :as gobj]
            ["@material-ui/core" :as mui]
            ["@material-ui/icons" :as mui-icons]
            [soul-talk.common.views :as c])
  (:import goog.History))

(defn login-styles [^js/Mui.Theme theme]
  #js {:root   #js {:height "100vh"}
       :image  #js {:backgroundImage    "url(https://source.unsplash.com/random)"
                    :backgroundRepeat   "no-repeat"
                    :backgroundSize     "cover"
                     ;:backgroundColor    (if (= "light" (-> theme .-palette .-type))
                     ;                      (-> theme .-palette .-grey .-50)
                     ;                      (-> theme .-palette .-grey .-900))
                    :backgroundPosition "center"}
       :paper  #js {:margin        (.spacing theme 8 4)
                    :display       "flex"
                    :flexDirection "column"
                    :alignItems    "center"}
       :avatar #js {:margin          (.spacing theme 1)
                    :backgroundColor (.-secondary theme)}
       :form   #js {:width     "100%"
                    :marginTop (.spacing theme 1)}
       :submit #js {:margin (.spacing theme 3 0 2)}
       :footer #js {:padding   (.spacing theme 3 2)
                    :marginTop "auto"}})

(defn form []
  (let [site-info (subscribe [:site-info])
        login-user (r/atom {:email "" :password ""})
        email (r/cursor login-user [:email])
        password (r/cursor login-user [:password])
        classes (login-styles styles/custom-theme)]
    [:> mui/Grid {:container  true
                  :component  "main"
                  :style (.-root classes)}
     [:> mui/CssBaseline]
     [:> mui/Grid {:item       true
                   :xs         false
                   :sm         4
                   :md         7
                   :style (.-image classes)}]
     [:> mui/Grid {:item      true
                   :xs        12
                   :sm        8
                   :md        5
                   :component mui/Paper
                   :square    true}

      [:div {:style (.-paper classes)}
       [:> mui/Avatar {:style (.-avatar classes)}
        [:> mui-icons/Home]]
       [:> mui/Typography {:component "h1"
                           :variant   "h4"}
        (:name @site-info)]
       [:form {:style (.-form classes)}
        [:> mui/TextField {:variant       "outlined"
                           :margin        "normal"
                           :required      true
                           :full-width    true
                           :id            "email"
                           :name          "email"
                           :label         "邮箱地址"
                           :auto-complete "email"
                           :auto-focus    true
                           :on-change     #(reset! email (-> % .-target .-value))}]
        [:> mui/TextField {:variant       "outlined"
                           :margin        "normal"
                           :required      true
                           :full-width    true
                           :label         "密码"
                           :name          "password"
                           :id            "password"
                           :type          "password"
                           :auto-complete "current-password"
                           :on-change     #(reset! password (-> % .-target .-value))}]
        [:> mui/Button {:type       "button"
                        :full-width true
                        :variant    "contained"
                        :color      "primary"
                        :style (.-submit classes)
                        :on-click   #(dispatch [:login @login-user])}
         "登录"]
        [:> mui/Grid {:container true}
         [:> mui/Grid {:item true
                       :xs   true}]]
        [:> mui/Box {:mt 5}
         [c/copyright]]]]]]))

(defn login-page []
  [form])



