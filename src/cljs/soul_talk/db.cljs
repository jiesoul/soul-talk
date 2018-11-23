(ns soul-talk.db)

(def default-db
  {:user (js->clj js/user :keywordize-keys true)
   :login-events []})
