(ns soul-talk.db)

(def default-db
  {:active-page :home
   :pagination {:page     1
               :pre-page 6}
   :breadcrumb ["Home"]
   :login-events []
   :app-key "AppKey /XQ4OFeKXX2cuzufXwTFio+lkjJ6BcswnJTkOn8XOjs="
   :login-token ""})

(goog-define api-uri "http://localhost:3000")

