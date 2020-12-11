(ns soul-talk.auth.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [dispatch subscribe]]
            [soul-talk.routes :refer [navigate!]]
            [soul-talk.common.styles :as styles]
            [goog.object :as gobj]
            ["@material-ui/core" :as mui]
            ["@material-ui/core/styles" :refer [createMuiTheme withStyles makeStyles]]
            ["@material-ui/core/colors" :as mui-colors]
            ["@material-ui/icons" :as mui-icons]
            [soul-talk.common.views :as c])
  (:import goog.History))

(def ^:private input-component
  (r/reactify-component
    (fn [props]
      [:input (-> props
                (assoc :ref (:inputRef props))
                (dissoc :inputRef))])))

(def ^:private textarea-component
  (r/reactify-component
    (fn [props]
      [:textarea (-> props
                   (assoc :ref (:inputRef props))
                   (dissoc :inputRef))])))

(defn form [{:keys [classes] :as props}]
  (let [site-info (subscribe [:site-info])
        login-user (r/atom {:email "" :password ""})
        email (r/cursor login-user [:email])
        password (r/cursor login-user [:password])]
    (fn []
      (js/console.log classes)
      [:> mui/Grid {:container  true
                    :component  "main"
                    :class-name "login-root"}
       [:> mui/CssBaseline]
       [:> mui/Grid {:item       true
                     :xs         false
                     :sm         4
                     :md         7
                     :class-name "login-image"}]
       [:> mui/Grid {:item      true
                     :xs        12
                     :sm        8
                     :md        5
                     :component mui/Paper
                     :square    true}

        [:div {:class-name (.-paper classes)}
         [:> mui/Avatar {:class-name (.-avatar classes)}
          ]
         [:> mui/Typography {:component "h1"
                             :variant   "h5"}
          (str (:name @site-info) " 登录")]
         [:form {:class-name (.-form classes)}
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
                          :class-name (.-submit classes)
                          :on-click   #(dispatch [:login @login-user])}
           "登录"]
          [:> mui/Grid {:container true}
           [:> mui/Grid {:item true
                         :xs   true}]]
          [:> mui/Box {:mt 5}
           [c/copyright]]]]]])))

(defn sign-in []
  [:> (styles/with-custom-styles
        (r/reactify-component form))])



