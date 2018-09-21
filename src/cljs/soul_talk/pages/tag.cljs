(ns soul-talk.pages.tag
  (:require [reagent.core :as r]
            [soul-talk.pages.common :as c]
            [re-frame.core :refer [dispatch subscribe]]))

(defn add-page []
  (r/with-let
    [tag (r/atom {})
     error (subscribe [:error])]
    [c/modal
     [:div "Add Category"]
     [:div.form-group
      [c/text-input "Name" :name "please enter name" tag]
      (when @error
        [:div.alert.alert-danger @error])]
     [:div
      [:a.btn.btn-primary
       {:value "Add"
        :on-click #(dispatch [:add-category @tag])}
       "Add"]]]))
