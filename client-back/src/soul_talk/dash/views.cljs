(ns soul-talk.dash.views
  (:require [reagent.core :as r]
            ["@material-ui/core" :as mui]
            [soul-talk.common.views :as c]
            [soul-talk.common.styles :as styles]))

(def button (styles/with-custom-styled  #js {:root #js {:backgroundColor "blue"}}
              mui/Button))

(defn chart [{:keys [classes] :as props}]
  [:div {:class-name (.-root classes)}
   "ssdasfasdfasdff"])

(defn chart-styles [theme]
  #js {:root #js {:backgroundColor "red"}})

(defn dash [props]
  [c/layout props
   [:div "ssss"]])

(defn home []
  (styles/with-custom-styled styles/layout-styles dash))