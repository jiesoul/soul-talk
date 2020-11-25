(ns soul-talk.db)

(def default-db
  {:active-page :login
   :login-events []
   :login-token nil})

(goog-define site-uri "http://localhost:3000")