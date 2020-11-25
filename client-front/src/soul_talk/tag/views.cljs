(ns soul-talk.tag.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [dispatch subscribe]]
            [soul-talk.common.views :as c]
            [antd :as antd]
            ["@ant-design/icons" :as antd-icons]))

(def list-columns
  [{:title "名称" :dataIndex "name", :key "name", :align "center"}
   {:title  "操作" :dataIndex "actions" :key "actions" :align "center"
    :render (fn [_ article]
              (r/as-element
                (let [{:keys [id]} (js->clj article :keywordize-keys true)]
                  [:div
                   [:> antd/Button {:icon (r/as-element [:> antd-icons/EditOutlined])
                                    :size   "small"
                                    :target "_blank"
                                    :href   (str "#/tags/" id "/edit")}]
                   [:> antd/Divider {:type "vertical"}]
                   [:> antd/Button {:type     "danger"
                                    :icon     (r/as-element [:> antd-icons/DeleteOutlined])
                                    :size     "small"
                                    :on-click (fn []
                                                (r/as-element
                                                  (c/show-confirm
                                                    "删除"
                                                    (str "你确认要删除吗？")
                                                    #(dispatch [:tags/delete id])
                                                    #(js/console.log "cancel"))))}]])))}])

(defn tags-list []
  (r/with-let [tags (subscribe [:tags])]
    [:section
     [:> antd/Table {:dataSource (clj->js @tags)
                     :columns (clj->js list-columns)
                     :bordered true}]]))

(defn tags-page []
  [c/manager-layout
   [:> antd/Layout.Content {:className "site-layout-content"}
    [:section "查询条件"]
    [:> antd/Divider]
    [tags-list]]])




