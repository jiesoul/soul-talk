(ns soul-talk.common.styles
  (:require ["@material-ui/core" :as mui]
            ["@material-ui/core/locale" :as locale :refer [zhCN]]
            ["@material-ui/core/styles" :as mui-styles :refer [createMuiTheme withStyles makeStyles]]
            ["@material-ui/core/colors" :as mui-colors]
            ["@material-ui/icons" :as mui-icons]
            ["@chakra-ui/react" :refer [extendTheme]]
            [goog.object :as gobj]
            [reagent.core :as r]))

(def colors {:brand {:900 "#1a365d"
                     :800 "#153e75"
                     :700 "#2a69ac"}})

(def theme (extendTheme colors))

(def ^:private theme-provider* (r/adapt-react-class mui-styles/MuiThemeProvider))

(def custom-theme
  (createMuiTheme
    #js {:palette #js {:primary #js {:main (gobj/get (.-blue ^js/Mui.Colors mui-colors) 500)}}
         :typography #js {:fontSize 14}
         :spacing 4
         :props #js {:MuiButton #js {:size "small"}}}
    zhCN))

(defn theme-provider
  [theme & children]
  (into [theme-provider* {:theme theme}]
    (map r/as-element children)))

(defn with-custom-styled [styles component]
  [:> ((withStyles styles)
       (r/reactify-component component))])

(def drawer-width "240px")
(def app-bar-height 64)

(defn- layout-styles [^js/Mui.Theme theme]
  (let [transitions (.-transitions theme)
        breakpoints (.-breakpoints theme)]
    #js {:root             #js {:display "flex"}
         :toolbar          #js {:paddingRight 24}
         :toolbarIcon      (gobj/extend #js {:display        "flex"
                                             :alignItems     "center"
                                             :justifyContent "flex-end"
                                             :padding        "0 8px"}
                             (-> theme .-mixins .-toolbar))
         :appBar           #js {:zIndex     (+ 1 (-> theme .-zIndex .-drawer))
                                :transition (.create
                                              transitions
                                              #js ["width" "margin"]
                                              #js {:easing   (-> transitions .-easing .-sharp)
                                                   :duration (-> transitions .-duration .-enteringScreen)})}
         :menuButton       #js {:marginRight 36}
         :menuButtonHidden #js {:display "none"}
         :title            #js {:flexGrow 1}
         :drawerPaper      #js {:position "relative"
                                :whitespace "nowrap"
                                :width      drawer-width
                                :height "100vh"
                                :transition (.create
                                              transitions
                                              #js ["width"]
                                              #js {:easing   (-> transitions .-easing .-sharp)
                                                   :duration (-> transitions .-duration .-leavingScreen)})}
         :appBarSpacer     (-> theme .-mixins .-toolbar)
         :content          #js {:flexGrow 1
                                :padding  (.spacing theme 1)
                                :overflow "auto"}
         :container        #js {:padding    (.spacing theme 2)}
         }))

(defn paper-styles [^js/Mui.Theme theme]
  #js {:paper            #js {:padding       (.spacing theme 2)
                              :display       "flex"
                              :overflow      "auto"
                              :flexDirection "column"}})

(defn popover-styles [theme]
  #js {:paper #js {:border "1px solid #d3d4d5"}
       :root #js {"&:focus" #js {:backgroundColor (-> theme .-palette .-primary .-main)
                                 "& .MuiListItemIcon-root, & .MuiListItemText-primary"
                                                  #js {:color (-> theme .-palette .-common .-white)}}}})

(defn backdrop-styles [^js/Mui.Theme theme]
  #js {:backdrop #js {:zIndex (+ 1 (-> theme .-zIndex .-drawer))
                      :color "#fff"}})

(defn success-snackbar-styles [^js/Mui.Theme theme]
  #js {:root #js {:backgroundColor (.-green mui-colors)}})

(defn snackbar-styles [^js/Mui.Theme theme]
  #js {:root #js {:width "100%"
                  "& > * + *" #js {:marginTop (.spacing theme)}}})

(defn- edit-form-styles [^js/Mui.Theme theme]
  #js {:root #js {"& .MuiTextField-root" #js {:padding (.spacing theme 0.5)}}
       :buttons #js {"& > *" #js {:margin (.spacing theme 1)}
                     :textAlign "center"}
       :paper #js {:padding (.spacing theme 2)
                   :alignItems "center"}})

(defn- form-styles [^js/Mui.Theme theme]
  #js {:root #js {"& .MuiTextField-root" #js {:margin (.spacing theme 1)
                                              :width "25ch"}}
       :buttons #js {"& > *" #js {:margin (.spacing theme 1)}
                     :textAlign "right"}
       :paper #js {:padding (.spacing theme 1)
                   :alignItems "center"}})

(defn- table-styles [^js/Mui.Theme theme]
  #js {:paper #js {:padding (.spacing theme 0.5)
                   :margin-top "5px"}
       :table #js {:minWidth "100%"}
       :head  #js {:backgroundColor (-> theme .-palette .-common .-blue)
                   :color           (-> theme .-palette .-common .-black)}
       :body  #js {:fontSize 10}
       :row   #js {"&:nth-of-type(odd)" #js {:backgroundColor (-> theme .-palette .-action .-hover)}}})

(defn styled-layout [layout]
  (with-custom-styled layout-styles layout))

(defn styled-edit-form [form]
  (with-custom-styled edit-form-styles form))

(defn styled-form [form]
  (with-custom-styled form-styles form))

(defn styled-table [table]
  (with-custom-styled table-styles table))