(ns soul-talk.site-info.views
  (:require [soul-talk.common.views :as c]
            [re-frame.core :as rf]
            [antd :refer [Form Input Button]]
            ["@material-ui/core" :as mui]
            ["@material-ui/icons" :as mui-icons]
            [reagent.core :as r]
            [soul-talk.common.styles :as styles]))

(defn edit-form [{:keys [classes] :as props}]
  (let [site-info (rf/subscribe [:site-info])
        {:keys [id name logo author description]} @site-info]
    [:div {:class-name (.-paper classes)}
     [:> mui/Paper
      [:form {:class-name (.-root classes)
              :id         "site-info-edit-form"}
       [:> mui/TextField {:variant    "outlined"
                          :margin     "normal"
                          :required   true
                          :full-width true
                          :size       "small"
                          :label      "网站名称"
                          :name       "name"
                          :id         "name"
                          :value      name
                          :on-change  #(rf/dispatch [:site-info/set-attr :name (-> % .-target .-value)])}]
       [:> mui/TextField {:name       "logo"
                          :id         "logo"
                          :label      "网站图标"
                          :size       "small"
                          :margin     "normal"
                          :variant    "outlined"
                          :full-width true
                          :value      logo
                          :on-change  #(rf/dispatch [:site-info/set-attr :logo (-> % .-target .-value)])}]
       [:> mui/TextField {:name       "description"
                          :id         "description"
                          :label      "简介"
                          :size       "small"
                          :margin     "normal"
                          :variant    "outlined"
                          :full-width true
                          :value      description
                          :on-change  #(rf/dispatch [:site-info/set-attr :description (-> % .-target .-value)])}]
       [:> mui/TextField {:name       "author"
                          :id         "author"
                          :label      "作者"
                          :variant    "outlined"
                          :size       "small"
                          :margin     "normal"
                          :full-width true
                          :value      author
                          :rules      [{:required true}]
                          :on-change  #(rf/dispatch [:site-info/set-attr :author (-> % .-target .-value)])}]
       [:div {:style      {:margin "normal"}
              :class-name (.-buttons classes)}
        [:> mui/Button {:type    "reset"
                        :color   "default"
                        :size    "small"
                        :variant "outlined"}
         "重置"]
        [:> mui/Button {:type     "reset"
                        :variant  "outlined"
                        :size     "small"
                        :color    "primary"
                        :style    {:margin "0 8px"}
                        :on-click #(rf/dispatch [:site-info/update @site-info])}
         "保存"]]
       ]]]))

(defn edit-page [props]
  [c/layout props
   [:<>
    (styles/styled-edit-form edit-form)]])

(defn home []
  (styles/main edit-page))
