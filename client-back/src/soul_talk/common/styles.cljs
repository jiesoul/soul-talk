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
    #js {:palette #js {:primary #js {:main (gobj/get (.-purple ^js/Mui.Colors mui-colors) 500)}
                       :secondary #js {:main "#11cb5f"}}}))

(def drawer-width 240)

(defn main-styles [^js/Mui.Theme theme]
  #js {:root             #js {:display "flex"}
       :color "#1a73e8"
       :bgColor "#e8f0fe"

       :toolbar          #js {:paddingRight 24}
       :toolbarIcon      #js {:display        "flex"
                              :alignItems     "flex-end"
                              :justifyContent "flex-end"
                              :padding        "0 8px"}
       :appBar           #js {:zIndex (+ 1 (-> theme .-zIndex .-drawer))}
       :appBarShift      #js {:marginLeft drawer-width}
       :appBarSpacer     (-> theme .-mixins .-toolbar)
       :popover #js {:pointerEvents "none"}

       :drawer           #js {:width      drawer-width
                              :flexShrink 0}
       :drawerPaper      #js {:width drawer-width}
       :drawerContent    #js {:height "auto"}

       :listRoot #js {:width    "100%"
                      :maxWidth drawer-width
                      :flexGrow 1
                      :backgroundColor (-> theme .-palette .-background .-paper)}
       :listNested #js {:paddingLeft (.spacing theme 4)}

       :treeViewRoot #js {:width "100%"
                          :maxWidth drawer-width
                          :height "auto"
                          :backgroundColor (-> theme .-palette .-background .-paper)}
       :treeItemRoot #js {:color (-> theme .-palette .-text .-secondary)
                          "&:hover > $content" #js {:backgroundColor (-> theme .-palette .-action .-hover)}
                          "&:focus > $content, &$selected > $content" #js {:backgroundColor "var(--tree-view-bg-color)"
                                                                           :color "var(--tree-view-color)"}
                          "&:focus > $content $label, &:hover > $content $label, &$selected > $content $label" #js {:backgroundColor "transparent"}}
       :treeItemContent #js {:color (-> theme .-palette .-text .-secondary)
                             :borderTopRightRadius (.spacing theme 2)
                             :borderBottomRightRadius (.spacing theme 2)
                             :paddingRight (.spacing theme 2)
                             :fontWeight (-> theme .-typography .-fontWeightMedium)
                             "$expanded > &" #js {:fontWeight (-> theme .-typography .-fontWeightRegular)}}
       :treeItemGroup #js {:marginLeft 0
                           "& $content" #js {:paddingLeft (.spacing theme 2)}}
       :treeItemExpanded #js {}
       :treeItemSelected #js {}
       :treeItemLabel #js {:fontWeight "inherit"
                           :color "inherit"}
       :treeItemLabelRoot #js {:display "flex"
                               :alignItems "center"
                               :padding (.spacing theme 0.5 0)}
       :treeItemLabelIcon #js {:marginRight (.spacing theme 1)}
       :treeItemLabelText #js {:fontWeight "inherit"
                               :flexGrow 1}

       :content          #js {:flexGrow 1
                              :height   "100vh"
                              :overflow "auto"}

       :menuButton       #js {:marginRight 36}
       :menuButtonHidden #js {:display "none"}
       :title            #js {:flexGrow 1}


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