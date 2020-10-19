(ns soul-talk.article.layout
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [soul-talk.common.component :refer [header]]))

(defn article-errors [article]
  (->
    (b/validate
      article
      :title [[v/required :message "标题不能为空\n"]]
      :body [[v/required :message "内容不能为空\n"]])
    first
    (vals)))

(defn category-select [category categories]
  [:> js/antd.Select {:value        {:key @category}
                   :labelInValue true
                   :style        {:width 120 :padding "5px"}
                   :on-change    #(let [val (:key (js->clj % :keywordize-keys true))]
                                    (reset! category val))}
   [:> js/antd.Select.Option {:value ""} "选择分类"]
   (doall
     (for [{:keys [id name]} @categories]
       ^{:key id} [:> js/antd.Select.Option {:value id} name]))])

(defn edit-menu [article edited-article categories]
  (let [category (r/cursor edited-article [:category])]
    [:div {:style {:color "#FFF"}}
     [:> js/antd.Col {:span 2 :offset 2}
      [:h3 {:style {:color "#FFF"}}
       (if @article "修改文章" "写文章")]]
     [:> js/antd.Col {:span 16}
      [category-select category categories]
      [:> js/antd.Button {:ghost   true
                       :on-click #(if-let [error (r/as-element (article-errors @edited-article))]
                                    (rf/dispatch [:set-error error])
                                    (if @article
                                      (rf/dispatch [:articles/edit @edited-article])
                                      (rf/dispatch [:articles/add @edited-article])))}
       "保存"]]]))

(defn article-layout [article edited-article categories main]
  [:> js/antd.Layout
   [header
    [edit-menu article edited-article categories]]
   [:> js/antd.Layout.Content {:className "main"}
    main]])