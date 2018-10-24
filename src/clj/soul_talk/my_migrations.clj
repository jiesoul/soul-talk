(ns soul-talk.my-migrations
  (:require [ragtime.jdbc :as ragtime]
            [ragtime.repl :as repl]
            [clojure.string :refer [join]]
            [soul-talk.config :refer [env]]
            [taoensso.timbre :as log]))

(def migrations
  {"migrate"
   (fn [config _]
     (repl/migrate config))

   "rollback"
   (fn [config _]
     (repl/rollback config))})

(defn migrations? [[args]]
  (contains? (set (keys migrations)) args))

(defn migrate
  "args - vector of arguments e.g: [\"migrate\" \"001\"]
    opts - map of options specifying the database configuration.
    :database-url database url
    :migration-dir migration file directory"
  [args opts]
  (when-not (migrations? args)
    (throw
      (IllegalArgumentException.
        (str "不能识别参数:" (first args)
          ", 有效的参数是:" (join "," (keys migrations))))))
  (let [config {:datastore  (ragtime/sql-database {:connection-uri (:database-url opts)})
                :migrations (ragtime/load-resources (:migrations opts))}]
    ((get migrations (first args)) config args)))