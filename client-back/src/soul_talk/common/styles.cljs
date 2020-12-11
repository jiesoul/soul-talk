(ns soul-talk.common.styles
  (:require ["@material-ui/core" :as mui]
            ["@material-ui/core/styles" :as mui-styles :refer [createMuiTheme withStyles makeStyles]]
            ["@material-ui/core/colors" :as mui-colors]
            ["@material-ui/icons" :as mui-icons]
            [goog.object :as gobj]
            [reagent.core :as r]))

(def ^:private theme-provider* (r/adapt-react-class mui-styles/MuiThemeProvider))

(defn make-styles
  ([styles] (make-styles styles {}))
  ([styles opt] (let [use-styles (mui-styles/makeStyles styles (clj->js opt))]
                  use-styles)))

(defn with-styles
  ([styles] (with-styles styles {}))
  ([styles opt] (let [hoc (withStyles styles (clj->js opt))]
                  hoc)))

(def custom-theme
  (createMuiTheme
    #js {:palette #js {:primary #js {:main (gobj/get (.-red ^js/Mui.Colors mui-colors) 100)}}}))

(defn custom-styles [^js/Mui.Theme theme]
  #js {:button    #js {:margin (.spacing theme 1)}
       :textField #js {:width       100
                       :marginLeft  (.spacing theme 1)
                       :marginRight (.spacing theme 1)}

       :root      #js {:height "100vh"
                       :display "flex"}
       :image     #js {:backgroundImage    "url(https://source.unsplash.com/random)"
                       :backgroundRepeat   "no-repeat"
                       :backgroundSize     "cover"
                       :backgroundPosition "cover"}
       :paper     #js {:margin        (.spacing theme 8 4)
                       :display       "flex"
                       :flexDirection "column"
                       :alignItems    "center"}
       :avatar    #js {:margin (.spacing theme 1)}
       :form      #js {:width     "100%"
                       :marginTop (.spacing theme 1)}
       :submit    #js {:margin (.spacing theme 3 0 2)}})

(def with-custom-styles (withStyles custom-styles))

(def make-custom-styles (makeStyles custom-styles custom-theme))