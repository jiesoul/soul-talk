(ns soul-talk.dash.views
  (:require [reagent.core :as r]
            ["@material-ui/core" :as mui]
            [soul-talk.common.views :as c]
            [soul-talk.common.styles :as styles]))

(defn dash [{:keys [classes] :as props}]
  [c/layout props
   [:> mui/Grid {:container true
                 :spacing 3}
    [:> mui/Grid {:item true
                  :xs 12
                  :md 8
                  :lg 9}
     [:> mui/Paper {:class-name (.-fixedHeightPaper classes)}
      [:> mui/Chart ]]]]])

(defn dash-page []
  (styles/main dash))