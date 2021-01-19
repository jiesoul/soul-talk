(ns soul-talk.site-info.views
  (:require [soul-talk.common.views :as c]
            [re-frame.core :as rf]
            ["@material-ui/core" :as mui]
            ["@material-ui/icons" :as mui-icons]
            [reagent.core :as r]
            [soul-talk.common.styles :as styles]))

(defn edit-form [{:keys [classes] :as props}]
  (let [site-info (rf/subscribe [:site-info])
        {:keys [id name logo author description]} @site-info]
    [:> mui/Paper {:class-name (.-paper classes)}
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
                         :default-value      name
                         :on-change  #(rf/dispatch [:site-info/set-attr :name (-> % .-target .-value)])}]
      [:> mui/TextField {:name       "logo"
                         :id         "logo"
                         :label      "网站图标"
                         :size       "small"
                         :margin     "normal"
                         :variant    "outlined"
                         :full-width true
                         :default-value      logo
                         :on-change  #(rf/dispatch [:site-info/set-attr :logo (-> % .-target .-value)])}]
      [:> mui/TextField {:name       "description"
                         :id         "description"
                         :label      "简介"
                         :size       "small"
                         :margin     "normal"
                         :variant    "outlined"
                         :full-width true
                         :default-value      description
                         :on-change  #(rf/dispatch [:site-info/set-attr :description (-> % .-target .-value)])}]
      [:> mui/TextField {:name       "author"
                         :id         "author"
                         :label      "作者"
                         :variant    "outlined"
                         :size       "small"
                         :margin     "normal"
                         :full-width true
                         :default-value      author
                         :rules      [{:required true}]
                         :on-change  #(rf/dispatch [:site-info/set-attr :author (-> % .-target .-value)])}]
      [:div {:style      {:margin "normal"}
             :class-name (.-buttons classes)}
       [:> mui/Button {:type     "button"
                       :variant  "outlined"
                       :size     "small"
                       :color    "primary"
                       :on-click #(rf/dispatch [:site-info/update @site-info])}
        "保存"]]
      ]]))

(defn edit-page [props]
  [c/layout props
   [:<>
    (styles/styled-edit-form edit-form)]])

(defn home []
  (styles/styled-layout edit-page))
