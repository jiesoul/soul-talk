(ns soul-talk.pages.category
  (:require [soul-talk.pages.common :as c]
            [re-frame.core :refer [dispatch subscribe]]
            [reagent.core :as r]))


(defn add-page []
  (r/with-let
    [category (r/atom {})
     error (subscribe [:error])]
    [c/modal
     [:div "Add Category"]
     [:div.form-group
      [c/text-input "Name" :name "please enter name" category]
      (when @error
        [:div.alert.alert-danger.smaller @error])]
     [:div
      [:a.btn.btn-link
       {:on-click #(.back js/history)}
       "return"]
      [:a.btn.btn-primary
       {:value "Add"
        :on-click #(dispatch [:categories/add @category])}
       "Add"]]]))
