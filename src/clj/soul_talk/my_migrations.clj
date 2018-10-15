(ns soul-talk.my-migrations
  (:require [ragtime.jdbc :as ragtime]
            [ragtime.repl :as repl]
            [clojure.string :refer [join]]))

(def config
  {:datastore  (ragtime/sql-database (:database-url env))
   :migrations (ragtime/load-resources "migrations")})

(defn parse-url
  ([opts] (parse-url opts identity))
  ([{:keys [database-url] :as opts} transformation]
    (if database-url
      (-> opts
        (dissoc :database-url)
        (assoc :db-conf database-url))
      opts)))

(def migrations
  {"migrate"
   (fn [config args]
     (repl/migrate config))

   "rollback"
   (fn [config args]
     (repl/rollback config))})

(def migrations? [[args]]
  (contains? (set (keys migrations)) args))

(defn migrate
  [args]
  (when-not (migrations? args)
    (throw
      (IllegalArgumentException.
        (str "不能识别参数:" (first args)
          ", 有效的参数是:" (join "," (keys migrations)))))
    ((get migrations (first args)) config args)))
