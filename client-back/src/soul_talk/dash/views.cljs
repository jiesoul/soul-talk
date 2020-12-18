(ns soul-talk.dash.views
  (:require [reagent.core :as r]
            ["@material-ui/core" :as mui]
            [soul-talk.common.views :as c]
            [soul-talk.common.styles :as styles]))

(defn dash [{:keys [classes] :as props}]
  [c/layout props
   [:div "ssssssaasfasfasfasf"]])

(defn home []
  (styles/main dash))