(ns soul-talk.user
  (:require [soul-talk.components.common :as c]))

(defn change-pass []
  (fn []
    [:div#change-pass-modal.modal {:tab-index -1 :role "dialog"}
     [:div.modal-dialog {:role "document"}
      [:div.modal-header
       [:h5 "修改密码"]
       [:button.close {:type :button :data-dismiss "modal" :aria-label "Close"}
        [:span {:aria-hidden "true"} "&times;"]]]
      [:div.modal-body
       [:p "Modal body text goes here."]]
      [:div.modal-footer
       [:button.btn.btn-danger {:data-dismiss "modal"} "Close"]]]]))
