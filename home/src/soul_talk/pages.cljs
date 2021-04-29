(ns soul-talk.pages
  (:require [reagent.core :as r]
            [soul-talk.common.views :as c]
            ["semantic-ui-react" :refer [Container Segment Header Image Button Menu Divider Grid Item
                                         List Divider Icon Advertisement Card Statistic Comment]]))

(defn home []
  (let [size "hege"]
    [:> Segment.Group
     [:div {:class-name "random-back-image"}
      [:div {:text-align "center"
             :class-name "home-banner"}
       [c/app-bar]
       [:div {:style {:padding-top "20vh"}}
        [:> Header {:icon       true
                    :as         "h1"
                    :text-align "center"}
         [:> Icon {:name     "user"
                   :circular true
                   :size     size}]
         [:> Header.Content
          "JIESOUL"]
         [:> List {:horizontal true
                   :inverted   true}
          [:> List.Item {:size size}
           [:> Icon {:name "github"
                     :link true
                     :size size}]]]]]]]
     [:> Segment {:vertical    true}
      "ssdfasfasf"]
     [c/footer]]))


(defn articles []
  [:<>
   [:> Container
    [c/app-bar]
    [:> Divider]]
   [:> Container {:style {:margin-top "10px"
                          :min-height "80vh"}}
    [:> Grid {:columns "equal"}
     [:> Grid.Column
      [:> Card
       [:> Card.Content
        [:> Card.Header "站点统计"]]
       [:> Card.Content
        [:> Card.Header "JIESOUL"]
        [:> Card.Description
         [:> Statistic.Group {:size "mini"}
          [:> Statistic
           [:> Statistic.Value "1121"]
           [:> Statistic.Label "文章"]]
          [:> Statistic
           [:> Statistic.Value "1122"]
           [:> Statistic.Label "浏览"]]
          [:> Statistic
           [:> Statistic.Value "1121"]
           [:> Statistic.Label "评论"]]]]]]
      [:> Card
       [:> Card.Content
        [:> Card.Header "最新评论"]]
       [:> Card.Content
        [:> Comment.Group {:size "mini"}
         [:> Comment
          [:> Comment.Avatar {:as "a"}]
          [:> Comment.Content
           [:> Comment.Author {:as "a"} "tt"]
           [:> Comment.Metadata
            [:span "Today at 5:42PM"]]
           [:> Comment.Text "How artistic!"]]]]]]]
     [:> Grid.Column {:width 8}
      [:> Card {:style {:width "100%"}}
       [:> Card.Content
        [:> Card.Header "文章列表"]]
       [:> Card.Content
        [:> Item.Group {:divided true}
         [:> Item
          [:> Item.Content
           [:> Item.Header {:as "a"} "文章1111111111111111111111111111111111"]
           [:> Item.Meta
            [:span {:class-name "author"} "tt"]
            [:span {:class-name "date"} "2021..."]]]]
         [:> Item
          [:> Item.Content
           [:> Item.Header {:as "a"} "文章1111111111111111111111111111111111"]
           [:> Item.Meta
            [:span {:class-name "author"} "tt"]
            [:span {:class-name "date"} "2021..."]]]]
         ]]]]
     [:> Grid.Column
      [:> Card
       [:> Card.Content
        [:> Card.Header "归档"]]
       [:> Card.Content ]]
      [:> Card
       [:> Card.Content
        [:> Card.Header "标签"]]
       [:> Card.Content ]]
      [:> Card
       [:> Card.Content
        [:> Card.Header "系列"]]
       [:> Card.Content]]
      ]]]])

(defn series []
  [:<>
   [c/app-bar]
   [:> Segment {:placeholder true
                :basic true
                :inverted true}
    [:> Header {:icon true}
     "series"]
    [:> Button {:content "more"}]]])


(defn tags []
  [:<>
   [c/app-bar]
   [:> Segment {:placeholder true
                :basic true
                :inverted true
                }
    [:> Header {:icon true}
     "Tags"]
    [:> Button {:content "more"}]]])


(defn about []
  [:<>
   [:> Container
    [c/app-bar]
    [:> Segment {:placeholder true
                 :basic       true}
     [:> Header {:icon true}
      "About"]
     [:> Button {:content "more"}]]]])