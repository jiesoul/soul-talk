(ns soul-talk.register
  (:require [domina :as dom]
            [reagent.core :as reagent :refer [atom]]
            [soul-talk.auth-validate :refer [reg-errors]]
            [ajax.core :as ajax]
            [reagent.session :as session]
            [taoensso.timbre :as log]
            [soul-talk.components.common :as c])
  (:import goog.History))


(defn register! [reg-date errors]
  (reset! errors (reg-errors @reg-date))
  (when-not @errors
    (ajax/POST "/register"
               {:format          :json
                :headers         {"Accept" "application/transit+json"}
                :params          @reg-date
                :handler         #(do
                                    (session/put! :identity (:email @reg-date))
                                    (reset! reg-date {})
                                    (js/alert "注册成功")
                                    (set! (.. js/window -location -href) "/dash"))
                :error-handler   #(do
                                    (log/error %)
                                    (reset!
                                        errors
                                        {:server-error (get-in % [:response :message])}))})))

(defn register-component []
  (let [reg-data (atom {})
        error (atom nil)]
    (fn []
      [c/modal
       "regModal"
       "Soul Talk Register"
       [:div
        [:div.well.well-sm "* 为必填"]
        [c/text-input "Email" :email "enter a email" reg-data]
        (when-let [error (first (:email @error))]
          [:div.alert.alert-danger error])
        [c/password-input "密码" :password "输入密码最少8位" reg-data]
        (when-let [error (first (:password @error))]
          [:div.alert.alert-danger error])
        [c/password-input "确认密码" :pass-confirm "确认密码和上面一样" reg-data]
        (when-let [error (first (:pass-confirm @error))]
          [:div.alert.alert-danger error])
        (when-let [error (:server-error @error)]
          [:div.alert.alert-danger error])]
       [:div
        [:input.btn.btn-primary.btn-block
         {:type     :submit
          :value    "注册"
          :on-click #(register! reg-data error)}]]])))

(defn reg-button []
  [:a.nav-link.p-2
   {:data-toggle "modal"
    :data-target "#regModal"}
   "注册"])