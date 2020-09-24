(ns soul-talk.session-tests
  (:require [clojure.test :refer :all]
            [buddy.core.codecs :refer :all]
            [buddy.auth :refer [throw-unauthorized]]
            [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]))


(defn make-request
  ([] {:session {}})
  ([id] {:session {:identity {:email "jiesoul@gmail.com"
                              :password "12345678"}}}))

(def backend (backends/session))
(def backend-with-authfn (backends/session {:authfn (constantly ::authorized)}))
