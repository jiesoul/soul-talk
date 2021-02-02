(ns soul-talk.site-info.views
  (:require [soul-talk.common.views :as c]
            [re-frame.core :as rf]
            ["semantic-ui-react" :as sui :refer [Grid Image Form Input Button Label]]
            ["react-toastify" :refer [toast]]
            [reagent.core :as r]
            [soul-talk.common.styles :as styles]))

(defn edit-form []
  (let [site-info (rf/subscribe [:site-info])
        {:keys [id name logo author description]} @site-info]
    (if @site-info
      [:> Form {:as "form"}
       [:> Form.Input
        {:required      true
         :name          "name"
         :label         "名称"
         :id            "name"
         :default-value name
         :on-change     #(rf/dispatch [:site-info/set-attr :name (-> % .-target .-value)])}]
       [:> Form.Input
        {:name          "logo"
         :label         "图标"
         :default-value logo
         :on-change     #(rf/dispatch [:site-info/set-attr :logo (-> % .-target .-value)])}]
       [:> Form.Input {:name          "description"
                  :label "简介"
                  :default-value description
                  :on-change     #(rf/dispatch [:site-info/set-attr :description (-> % .-target .-value)])}]
       [:> Form.Input {:name          "author"
                  :default-value author
                  :label "作者"
                  :on-change     #(rf/dispatch [:site-info/set-attr :author (-> % .-target .-value)])}]
       [:div {:style {:text-align "center"}}
        [:> Button {:color "green"
                    :basic    true
                    :size     "mini"
                    :icon "save"
                    :content "保存"
                    :on-click #(rf/dispatch [:site-info/update @site-info])}]]])))

(defn home []
  [c/layout [edit-form]])
