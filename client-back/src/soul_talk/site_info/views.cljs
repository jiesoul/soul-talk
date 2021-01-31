(ns soul-talk.site-info.views
  (:require [soul-talk.common.views :as c]
            [re-frame.core :as rf]
            ["@material-ui/core" :as mui]
            ["@material-ui/icons" :as mui-icons]
            ["semantic-ui-react" :as sui :refer [Grid Image Form Input Button Label]]
            [reagent.core :as r]
            [soul-talk.common.styles :as styles]))

(defn edit-form [{:keys [classes] :as props}]
  (let [site-info (rf/subscribe [:site-info])
        {:keys [id name logo author description]} @site-info]
    [:div
     [:> Form {:as "form"}
      [:> Form.Field {:required true}
       [:label "名称"]
       [:> Input
        {:required      true
         :name          "name"
         :id            "name"
         :default-value name
         :on-change     #(rf/dispatch [:site-info/set-attr :name (-> % .-target .-value)])}]]
      [:> Form.Field
       [:label "图标"]
       [:> Input
        {:name          "logo"
         :default-value logo
         :on-change     #(rf/dispatch [:site-info/set-attr :logo (-> % .-target .-value)])}]]
      [:> Form.Field
       [:label "简介"]
       [:> Input {:name          "description"
                          :default-value description
                          :on-change     #(rf/dispatch [:site-info/set-attr :description (-> % .-target .-value)])}]]
      [:> Form.Field
       [:label "作者"]
       [:> Input {:name          "author"
                          :default-value author
                          :on-change     #(rf/dispatch [:site-info/set-attr :author (-> % .-target .-value)])}]]
      [:> Button {:type     "button"
                  :color    "primary"
                  :basic true
                  :size "mini"
                  :on-click #(rf/dispatch [:site-info/update @site-info])}
       "保存"]
      ]]))

(defn edit-page [props]
  [c/layout props
   [:<>
    (styles/styled-edit-form edit-form)]])

(defn home []
  (styles/styled-layout edit-page))
