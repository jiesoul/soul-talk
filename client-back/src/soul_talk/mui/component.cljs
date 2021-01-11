(ns soul-talk.mui.component
  (:require ["@material-ui/core" :refer [Dialog DialogTitle Typography IconButton DialogContent DialogActions
                                        Button]]
            ["@material-ui/icons" :refer [Close]]))

(defn dialog [{:keys [open title cancel-text ok-text on-close on-ok] :as opts} & children]
  [:> Dialog {:aria-labelledby        "alert-dialog-title"
                  :aria-describedby       "alert-dialog-description"
                  :disable-backdrop-click true
                  :style                  {:min-width "200px"}
                  :open                   open
                  :key (str "dialog" (random-uuid))}
   [:> DialogTitle {:id                 "alert-dialog-title"
                        :disable-typography true
                        :style              {:margin  0
                                             :padding "5px"}}
    [:> Typography {:variant "h6"} title]
    [:> IconButton {:aria-label "close"
                        :on-click   on-close
                        :style      {:position "absolute"
                                     :right    "5px"
                                     :top      "1px"}}
     [:> Close]]]
   [:> DialogContent {:dividers true}
    children]
   [:> DialogActions
    [:> Button {:on-click on-close :color "default"} (if cancel-text cancel-text "取消")]
    [:> Button {:on-click on-ok :color "primary"} (if ok-text ok-text "保存")]]])
