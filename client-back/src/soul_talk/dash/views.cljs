(ns soul-talk.dash.views
  (:require [reagent.core :as r]
            ["@material-ui/core" :as mui]
            [soul-talk.common.views :as c]
            [soul-talk.common.styles :as styles :refer [with-styles]]))

(def button (styles/styled mui/Button #js {:root #js {:backgroundColor "blue"}}))

(defn chart [{:keys [classes children] :as props}]
  [:div {:class-name (.-root classes)}
   "ssdasfasdfasdff"])

(defn chart-styles [theme]
  #js {:root #js {:backgroundColor "red"}})

(defn dash [props]
  [c/layout props
   [((styles/with-styles chart-styles) chart)]])

(defn home []
  (styles/main dash))