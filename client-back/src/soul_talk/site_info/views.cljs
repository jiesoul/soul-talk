(ns soul-talk.site-info.views
  (:require [soul-talk.common.views :as c]
            [re-frame.core :as rf]
            [antd :refer [Form Input Button]]
            ["@material-ui/core" :as mui]
            ["@material-ui/icons" :as mui-icons]
            [reagent.core :as r]
            [soul-talk.common.styles :as styles]))

(defn edit-form [{:keys [classes] :as props}]
  (let [site-info (rf/subscribe [:site-info])]
    (fn []
      (c/layout props
        (let [{:keys [id name logo author description]} @site-info]
          [:div {:class-name (.-root classes)
                 :style      {:text-align "center"}}
           [:form {:class-name (.-form classes)
                   :id         "site-info-edit-form"}
            [:> mui/TextField {:variant    "outlined"
                               :margin     "normal"
                               :required   true
                               :full-width true
                               :label      "网站名称"
                               :name       "name"
                               :id         "name"
                               :value      name
                               :on-change  #(rf/dispatch [:site-info/set-attr :name (-> % .-target .-value)])}]
            [:> mui/TextField {:name       "logo"
                               :label      "网站图标"
                               :margin     "normal"
                               :variant    "outlined"
                               :full-width true
                               :value logo
                               :on-change  #(rf/dispatch [:site-info/set-attr :logo (-> % .-target .-value)])}]
            [:> mui/TextField {:name       "description"
                               :label      "简介"
                               :margin     "normal"
                               :variant    "outlined"
                               :full-width true
                               :value description
                               :on-change  #(rf/dispatch [:site-info/set-attr :description (-> % .-target .-value)])}]
            [:> mui/TextField {:name       "author"
                               :label      "作者"
                               :variant    "outlined"
                               :margin     "normal"
                               :full-width true
                               :value author
                               :rules      [{:required true}]
                               :on-change  #(rf/dispatch [:site-info/set-attr :author (-> % .-target .-value)])}]
            [:div {:style {:margin     "normal"}}
             [:> mui/Button {:type "reset"
                             :on-click #(js/console.log "object: " %)}
              "重置"]
             [:> mui/Button {:type     "button"
                             :style    {:margin "0 8px"}
                             :on-click #(rf/dispatch [:site-info/update @site-info])}
              "保存"]]
            ]])))))

(defn home []
  (styles/main edit-form))
