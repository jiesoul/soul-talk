(ns soul-talk.mui.styles
  (:require ["@material-ui/core/styles" :as mui-styles]
            [soul-talk.mui.util :as util]))

(def ^:private theme-provider* (util/adapt-react-class mui-styles/MuiThemeProvider "mui-theme-provider"))

