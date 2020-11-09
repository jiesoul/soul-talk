(ns soul-talk.database.db-test
  (:require [clojure.test :refer :all]
            [soul-talk.database.db :refer :all]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [soul-talk.env :refer [conf]]
            [mount.core :as mount]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
      #'soul-talk.env/conf
      #'soul-talk.database.db/*db*)
    (f)))