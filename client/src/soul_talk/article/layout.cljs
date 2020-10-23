(ns soul-talk.article.layout
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [soul-talk.common.component :refer [header]]
            [antd :as antd]))

(defn article-errors [article]
  (->
    (b/validate
      article
      :title [[v/required :message "标题不能为空\n"]]
      :body [[v/required :message "内容不能为空\n"]])
    first
    (vals)))

(defn category-select [category categories]
  [:> antd/Select {:value        {:key @category}
                   :labelInValue true
                   :style        {:width 120 :padding "5px"}
                   :on-change    #(let [val (:key (js->clj % :keywordize-keys true))]
                                    (reset! category val))}
   [:> antd/Select.Option {:value ""} "选择分类"]
   (doall
     (for [{:keys [id name]} @categories]
       ^{:key id} [:> antd/Select.Option {:value id} name]))])

(defn edit-menu []
  (r/with-let [article [rf/subscribe [:article]]]
    [:> antd/Col {:span 2 :offset 2}
     [:h3 {:style {:color "#FFF"}}
      (if @article "修改文章" "写文章")]]
    [:> antd/Col {:span 16}
     [:> antd/Button {:ghost    true
                      :on-click #(if @article
                                  (rf/dispatch [:articles/edit @article])
                                  (rf/dispatch [:articles/add @article]))}
      "保存"]]))

(defn article-layout [article edited-article main]
  [:> antd/Layout
   [header
    [edit-menu]]
   [:> antd/Layout.Content {:className "main"}
    main]])