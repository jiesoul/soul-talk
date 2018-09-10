(ns soul-talk.post
  (:require [reagent.core :as r]
            [ajax.core :as ajax :refer [GET POST]]
            [taoensso.timbre :as log])
  (:import [goog.History]))

(defonce posts-data (r/atom nil))

(defn posts-list [posts]
  (fn []
    [:table.table.table-striped
     [:thead
      [:tr
       [:th "序号"]
       [:th "title"]
       [:th "create_time"]
       [:th "modify_time"]
       [:th "publish"]
       [:th "author"]
       [:th "counter"]]]
     [:tbody
      (for [{:keys [title create_time  modify_time publish author counter] :as post} posts]
        ^{:key post}
        [:tr
         [:td title]
         [:td create_time]
         [:td modify_time]
         [:td publish]
         [:td author]
         [:td counter]
         [:td counter]])]]))

(defn posts-component []
  (fn []
    [:div.container-fluid
     [:h3 "Post List"]
     [:hr]
     [posts-list @posts-data]]))

(defn load []
  (GET
    "/posts"
    {:handle #(do
                (js/alert %)
                (reset! posts-data (get-in % [:response "posts"])))}
    {:error-handler #(js/alert %)}))