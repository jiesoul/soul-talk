(ns soul-talk.db)

(def default-db
  {:active-page :home
   :pagination {:page     1
               :pre-page 6}
   :breadcrumb ["Home"]
   :login-events []})

(def api-uri js/soul_talk.api_url)

