(ns soul-talk.components.common
  (:import [goog History])
  (:require [goog.history.EventType :as EventType]
            [goog.events :as events]))

(defn input [type id placeholder fields]
  "标准 input， 其中的 on-change 实现了值的绑定"
  [:input.form-control.input-lg
   {:type type
    :placeholder placeholder
    :value (id @fields)
    :on-change #(swap! fields assoc id (-> % .-target .-value))}])

(defn form-input [type label id placeholder fields optional?]
  [:div.form-group
   [:label label]
   (if optional?
     [input type id placeholder fields]
     [:div.input-group
      [input type id placeholder fields]
      [:span.input-group-addon.text-danger "*"]])])

(defn text-input [label id placeholder fields & [optional?]]
  (form-input :text label id placeholder fields optional?))

(defn password-input [label id placeholder fields & [optional?]]
  (form-input :password label id placeholder fields optional?))


(defn modal [id header body footer]
  [:div.modal.fade {:id id}
   [:div.modal-dialog {:role "document"}
    [:div.modal-content
     [:div.modal-header
      [:h3.mb-3.font-weight-normal.text-center header]
      [:button.close
       {:data-dismiss "modal"
        :aria-label "Close"
        :type :button}
       [:i.fa.fa-times {:aria-hidden "true"}]]]
     [:div.modal-body body]
     [:div.modal-footer footer]]]])

(defonce h (History.))

;(defn navigate-to! [routes nav]
;  (.setToken h (nav-to-url routes nav)))
;
;(defn hook-browser-navigation!
;  [routes]
;  (doto h
;    (events/listen
;      EventType/NAVIGATE
;      (fn [event]
;        (let [path (.-token event)
;              {:keys [page params] :as nav} (url-to-nav routes path)]
;          (if page
;            (reset! navigational-statie nav)
;            (do
;              (.warn js/console (str "No route matches token " path ", redirecting to "))
;              (navigate-to! routes {:page :dash}))))))
;    (.setEnabled true)))