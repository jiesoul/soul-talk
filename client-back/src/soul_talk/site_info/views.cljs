(ns soul-talk.site-info.views
  (:require [soul-talk.common.views :as c]
            [re-frame.core :as rf]
            [antd :refer [Form Input Button]]
            [reagent.core :as r]))

(defn edit-form []
  (let [site-info (rf/subscribe [:site-info])]
    (fn []
      (c/manager-layout
        [:> Form {:name       "site-info-edit-form"
                  :labelCol   {:span 8}
                  :wrapperCol {:span 8}
                  :initial-values @site-info
                  :validate-messages c/validate-messages}
         [:> Form.Item {:name "name"
                        :label "网站名称"
                        :rules [{:required true}]
                        :on-change #(rf/dispatch [:site-info/set-attr :name (-> % .-target .-value)])}
          [:> Input]]
         [:> Form.Item {:name      "logo"
                        :label     "网站图标"
                        :rules     [{:type :url}]
                        :on-change #(rf/dispatch [:site-info/set-attr :logo (-> % .-target .-value)])}
          [:> Input]]
         [:> Form.Item {:name "description"
                        :label "简介"
                        :on-change #(rf/dispatch [:site-info/set-attr :description (-> % .-target .-value)])}
          [:> Input]]
         [:> Form.Item {:name "author"
                        :label "作者"
                        :rules [{:required true}]
                        :on-change #(rf/dispatch [:site-info/set-attr :author (-> % .-target .-value)])}
          [:> Input]]
         [:> Form.Item {:wrapperCol {:span 8 :offset 8}
                        :style {:text-align "right"}}
          ;[:> Button {:htmlType "button"
          ;            :on-click #(js/console.log "object: " %)}
          ; "重置"]
          [:> Button {:type "primary"
                      :htmlType "submit"
                      :style {:margin "0 8px"}
                      :on-click #(rf/dispatch [:site-info/update @site-info])}
           "保存"]]
         ]))))
