(ns soul-talk.views
  (:require [reagent.core :as r]
            [soul-talk.pages.home :refer [home-page]]
            [soul-talk.pages.dash :refer [dash-page]]
            [soul-talk.pages.auth :refer [login-page]]
            [taoensso.timbre :as log]
            [re-frame.core :as rf]
            [clojure.string :as str]))

(defmulti pages (fn [page _] page))

(defmethod pages :home [_ _] [home-page])
(defmethod pages :dash [_ _] [dash-page])
(defmethod pages :login [_ _] [login-page])


(defmethod pages :default [_ _] [:div "default show ......"])

(defn main-page []
  (log/info "load main-page")
  (pages :home))

(defn clock
  []
  [:div.example-clock
   {:style {:color @(rf/subscribe [:time-color])}}
   (-> @(rf/subscribe [:time])
       .toTimeString
       (str/split " ")
       first)])

(defn color-input
  []
  [:div.color-input
   "Time color: "
   [:input {:type "text"
            :value @(rf/subscribe [:time-color])
            :on-change #(rf/dispatch [:time-color-change (-> % .-target .-value)])}]])  ;; <---

(defn ui
  []
  [:div
   [:h1 "Hello world, it is now"]
   [clock]
   [color-input]])