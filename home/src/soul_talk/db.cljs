(ns soul-talk.db)

(def default-db
  {:active-page :home
   :navs [{:key :home :name "主页" :url "/"}
          {:key :articles :name "文章" :url "/articles"}
          {:key :about :name "关于" :url "/about"}]
   :contacts [{:name "github" :icon "github" :url "https://github.com/jiesoul"}
              {:name "weibo" :icon "weibo" :url "https://weibo.com/jiesoul"}
              {:name "Email" :icon "mail" :url "mailto:jiesoul@gmail.com"}]})

(goog-define api-url "http://localhost:3001/v1")
(goog-define api-key "pmyzXOP27cbvyyqDuEWGM1WAy4Bw1UKK_qpYzfP63rk")


