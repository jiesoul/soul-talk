(ns soul-talk.dash.views
  (:require [reagent.core :as r]
            ["@material-ui/core" :as mui]
            [soul-talk.common.views :as c]
            [soul-talk.common.styles :as styles]))

(defn chart-styles [theme]
  #js {:root #js {:backgroundColor "red"}})

(defn chart [{:keys [classes] :as props}]
  [:div {:class-name (.-root classes)}
   "ssdasfasdfasdff"])

(defn dash [props]
  [c/layout props
   (styles/with-custom-styled chart-styles chart)])

(defn home []
  (styles/styled-layout dash))