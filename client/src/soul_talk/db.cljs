(ns soul-talk.db)

(def default-db
  {:active-page :home
   :pagination {:page     1
               :pre-page 6}
   :breadcrumb ["Home"]
   :login-events []})

(goog-define api-uri "http://localhost:3000/api/v1")

