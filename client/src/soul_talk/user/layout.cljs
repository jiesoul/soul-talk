(ns soul-talk.user.layout
  (:require [soul-talk.common.component :refer [footer]]
            [reagent.core :as r]
            [antd :as antd]))

(defn copyright []
  [:> antd/Layout.Footer
   "Copyright "
   [:> antd/Icon {:type "copyright"}]
   " 2019 "])

(defn user-layout [children]
  [:> antd/Layout {:title ""}
   [:> antd/Layout.Content {:style {:min-height "100vh"
                                    :padding "24px 0 20px 0"}}
    children
    copyright]])
