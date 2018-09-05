(ns user
  (:require [soul-talk.models.db :as db :refer [db-spec]]
            [ragtime.jdbc :as jdbc]
            [ragtime.repl :as rag-repl]))

(def config
  {:datastore  (jdbc/sql-database db-spec)
   :migrations (jdbc/load-resources "migrations")})
