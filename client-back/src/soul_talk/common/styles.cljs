(ns soul-talk.common.styles
  (:require ["@material-ui/core" :as mui]
            ["@material-ui/core/styles" :as mui-styles :refer [createMuiTheme withStyles makeStyles]]
            ["@material-ui/core/colors" :as mui-colors]
            ["@material-ui/icons" :as mui-icons]
            [goog.object :as gobj]
            [reagent.core :as r]))

(def ^:private theme-provider* (r/adapt-react-class mui-styles/MuiThemeProvider))

(def custom-theme
  (createMuiTheme
    #js {:palette #js {:primary #js {:main (gobj/get (.-red ^js/Mui.Colors mui-colors) 100)}}}))

(defn login-styles [^js/Mui.Theme theme]
  #js {:button    #js {:margin (.spacing theme 1)}
       :textField #js {:width       100
                       :marginLeft  (.spacing theme 1)
                       :marginRight (.spacing theme 1)}
       :root      #js {:height "100vh"}
       :image     #js {:backgroundImage    "url(https://source.unsplash.com/random)"
                       :backgroundRepeat   "no-repeat"
                       :backgroundSize "cover"
                       :backgroundPosition "center"}
       :paper     #js {:margin        (.spacing theme 8 4)
                       :display       "flex"
                       :flexDirection "column"
                       :alignItems    "center"}
       :avatar    #js {:margin (.spacing theme 1)
                       :backgroundColor (.-secondary theme)}
       :form      #js {:width     "100%"
                       :marginTop (.spacing theme 1)}
       :submit    #js {:margin (.spacing theme 3 0 2)}})

(def drawer-width 240)

(defn main-styles [^js/Mui.Theme theme]
  #js {:root        #js {:display "flex"}
       :toolbar     #js {:paddingRight 24}
       :toolbarIcon #js {:display        "flex"
                         :alignItems     "flex-end"
                         :justifyContent "flex-end"
                         :padding        "0 8px"}
       :appBar      #js {:zIndex (+ 1 (-> theme .-zIndex .-drawer))}
       :appBarShift #js {:marginLeft drawer-width}
       :menuButton #js {:marginRight 36}
       :menuButtonHidden #js {:display "none"}
       :title #js {:flexGrow 1}
       :drawerPaper #js {:position "relative"
                     :width drawer-width}
       :drawerPaperClose #js {:overflow "hidden"
                          :width (.spacing theme 7)}
       :appBarSpacer (-> theme .-mixins .-toolbar)
       :content #js {:flexGrow 1
                 :height "100vh"
                 :overflow "auto"}
       :container #js {:padding (.spacing theme 2)
                   :display "flex"
                   :overflow "auto"
                   :flexDirection "column"}
       :fixedHeight #js {:height 240}})

(def with-login-styles (withStyles login-styles))

(def with-main-styles (withStyles main-styles))

(def make-custom-styles (makeStyles (login-styles custom-theme)))

(defn sign-in [component]
  [:> (with-login-styles
        (r/reactify-component component))])

(defn main [component]
  [:> (with-main-styles
        (r/reactify-component component))])