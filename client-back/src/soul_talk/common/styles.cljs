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
    #js {:palette #js {:primary #js {:main (gobj/get (.-blue ^js/Mui.Colors mui-colors) 100)}}}))



(def drawer-width 240)

(defn main-styles [^js/Mui.Theme theme]
  #js {:root             #js {:display "flex"}
       :nested           #js {:paddingLeft (.spacing theme 4)
                              }
       :drawer           #js {:width      drawer-width
                              :flexShrink 0}
       :drawerPaper      #js {:width drawer-width}
       :drawerContent    #js {:height "auto"}
       :content          #js {:flexGrow 1
                              :height   "100vh"
                              :overflow "auto"}

       :toolbar          #js {:paddingRight 24}
       :toolbarIcon      #js {:display        "flex"
                              :alignItems     "flex-end"
                              :justifyContent "flex-end"
                              :padding        "0 8px"}
       :appBar           #js {:zIndex (+ 1 (-> theme .-zIndex .-drawer))}
       :appBarShift      #js {:marginLeft drawer-width}
       :menuButton       #js {:marginRight 36}
       :menuButtonHidden #js {:display "none"}
       :title            #js {:flexGrow 1}

       :appBarSpacer     (-> theme .-mixins .-toolbar)

       :container        #js {:padding       (.spacing theme 3)
                              :display       "flex"
                              :overflow      "auto"
                              :flexDirection "column"}
       :fixedHeight      #js {:height 240}
       })

(defn with-custom-styles [component styles]
  [:> ((withStyles styles)
       (r/reactify-component component))])

(defn main [component]
  (with-custom-styles component main-styles))