(ns soul-talk.common.styles
  (:require ["@material-ui/core" :as mui]
            ["@material-ui/core/locale" :as locale :refer [zhCN]]
            ["@material-ui/core/styles" :as mui-styles :refer [createMuiTheme withStyles makeStyles]]
            ["@material-ui/core/colors" :as mui-colors]
            ["@material-ui/icons" :as mui-icons]
            [goog.object :as gobj]
            [reagent.core :as r]))

(def ^:private theme-provider* (r/adapt-react-class mui-styles/MuiThemeProvider))

(def custom-theme
  (createMuiTheme
    #js {:palette #js {:primary #js {:main (gobj/get (.-blue ^js/Mui.Colors mui-colors) 500)}}
         :typography #js {:fontSize 12}
         :spacing 4
         :props #js {:MuiButton #js {:size "small"}}}
    zhCN))

(defn theme-provider
  [theme & children]
  (into [theme-provider* {:theme theme}]
    (map r/as-element children)))

(defn- apply-hoc [hoc component]
  (println "component: " component)
  (println "hoc: " hoc)
  (-> component
    (r/reactify-component)
    (hoc)
    (r/adapt-react-class)))

(defn with-styles
  ([styles] (with-styles styles {}))
  ([styles opts]
   (let [hoc (mui-styles/withStyles styles opts)]
     (partial apply-hoc hoc))))

(defn with-custom-styled
  [styles component opts]
  (if opts
    ((with-styles styles opts) component)
    ((with-styles styles) component)))

(defn styled [component styles]
  ((mui-styles/styled component) styles))

(defn with-custom-styles [component styles]
  [:> ((withStyles styles)
       (r/reactify-component component))])

(def drawer-width 200)
(def app-bar-height 64)

(defn main-styles [^js/Mui.Theme theme]
  (let [transitions (.-transitions theme)
        breakpoints (.-breakpoints theme)]
    #js {:root              #js {:display "flex"}
         :color             "#1a73e8"
         :bgColor           "#e8f0fe"

         :toolbar           #js {:paddingRight 24}
         :toolbarIcon       #js {:display        "flex"
                                 :alignItems     "flex-end"
                                 :justifyContent "flex-end"
                                 :padding        "0 8px"}

         :appBar            #js {:zIndex (+ 1 (-> theme .-zIndex .-drawer))}
         :appBarShift       #js {:marginLeft drawer-width
                                 :transition (.create
                                               transitions
                                               #js ["width" "margin"]
                                               #js {:easing   (-> transitions .-easing .-sharp)
                                                    :duration (-> transitions .-duration .-enteringScreen)})}
         :menuButton        #js {:marginRight 36}
         :menuButtonHidden  #js {:display "none"}


         :title             #js {:flexGrow 1}

         :popover           #js {:pointerEvents "none"}

         :drawer            #js {:width      drawer-width
                                 :flexShrink 0}
         :drawerPaper       #js {:width drawer-width}

         :drawerPaperClose  #js {:overflowX                  "hidden"
                                 :width                      (.spacing theme 7)
                                 :transition                 (.create
                                                               transitions
                                                               #js ["width"]
                                                               #js {:easing   (-> transitions .-easing .-sharp)
                                                                    :duration (-> transitions .-duration .-leavingScreen)})
                                 "@media (min-width: 600px)" #js {:width (.spacing theme 9)}}

         :drawerContainer   #js {:overflow "auto"}

         :appBarSpacer      (-> theme .-mixins .-toolbar)

         :drawerContent     #js {:height "auto"}

         :listRoot          #js {:width           "100%"
                                 :maxWidth        drawer-width
                                 :flexGrow        1
                                 :backgroundColor (-> theme .-palette .-background .-paper)}
         :listNested        #js {:paddingLeft (.spacing theme 4)}

         :treeViewRoot      #js {:width           "100%"
                                 :maxWidth        drawer-width
                                 :height          "auto"
                                 :backgroundColor (-> theme .-palette .-background .-paper)}
         :treeItemRoot      #js {:color                                                                               (-> theme .-palette .-text .-secondary)
                                 "&:hover > $content"                                                                 #js {:backgroundColor (-> theme .-palette .-action .-hover)}
                                 "&:focus > $content, &$selected > $content"                                          #js {:backgroundColor "var(--tree-view-bg-color)"
                                                                                                                           :color           "var(--tree-view-color)"}
                                 "&:focus > $content $label, &:hover > $content $label, &$selected > $content $label" #js {:backgroundColor "transparent"}}
         :treeItemContent   #js {:color                   (-> theme .-palette .-text .-secondary)
                                 :borderTopRightRadius    (.spacing theme 2)
                                 :borderBottomRightRadius (.spacing theme 2)
                                 :paddingRight            (.spacing theme 2)
                                 :fontWeight              (-> theme .-typography .-fontWeightMedium)
                                 "$expanded > &"          #js {:fontWeight (-> theme .-typography .-fontWeightRegular)}}
         :treeItemGroup     #js {:marginLeft  0
                                 "& $content" #js {:paddingLeft (.spacing theme 2)}}
         :treeItemExpanded  #js {}
         :treeItemSelected  #js {}
         :treeItemLabel     #js {:fontWeight "inherit"
                                 :color      "inherit"}
         :treeItemLabelRoot #js {:display    "flex"
                                 :alignItems "center"
                                 :padding    (.spacing theme 0.5 0)}
         :treeItemLabelIcon #js {:marginRight (.spacing theme 1)}
         :treeItemLabelText #js {:fontWeight "inherit"
                                 :flexGrow   1}

         :mainContainer     #js {:padding (.spacing theme 1)
                                 :flexGrow 1
                                 :height   "100vh"}

         :footer #js {:margin-bottom "1px"}

         :breadcrumb        #js {:font-weight "1"}

         :content           #js {:flexGrow 1
                                 :padding  (.spacing theme 1)
                                 :overflow "auto"}

         :container         #js {:paddingTop    (.spacing theme 4)
                                 :paddingBottom (.spacing theme 4)}

         :paper             #js {:padding       (.spacing theme 2)
                                 :display       "flex"
                                 :overflow      "auto"
                                 :flexDirection "column"}
         :fixedHeight       #js {:height 240}
         }))

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

(defn edit-form-styles [^js/Mui.Theme theme]
  #js {:root #js {"& .MuiTextField-root" #js {:padding (.spacing theme 0.5)}}
       :buttons #js {"& > *" #js {:margin (.spacing theme 1)}
                     :textAlign "right"}
       :paper #js {:padding (.spacing theme 2)}})

(defn form-styles [^js/Mui.Theme theme]
  #js {:root #js {"& .MuiTextField-root" #js {:margin (.spacing theme 1)
                                              :width "25ch"}}
       :buttons #js {"& > *" #js {:margin (.spacing theme 1)}
                     :textAlign "right"}
       :paper #js {:padding (.spacing theme 1)}})

(defn table-styles [^js/Mui.Theme theme]
  #js {:paper #js {:padding (.spacing theme 0.5)
                   :margin-top "5px"}
       :table #js {:minWidth "100%"}
       :head  #js {:backgroundColor (-> theme .-palette .-common .-blue)
                   :color           (-> theme .-palette .-common .-black)}
       :body  #js {:fontSize 10}
       :row   #js {"&:nth-of-type(odd)" #js {:backgroundColor (-> theme .-palette .-action .-hover)}}})



(defn main [component]
  (with-custom-styles component main-styles))

(defn styled-edit-form [form]
  (with-custom-styles form edit-form-styles))

(defn styled-form [form]
  (with-custom-styles form form-styles))

(defn styled-table [table]
  (with-custom-styles table table-styles))