(ns soul-talk.home.db
  (:require [reagent.core :as r]
            ["@ant-design/icons" :as antd-icons]))

(def resources-data
  [{:title "Clojure" :href "https://www.clojure.org"}
   {:title "ClojureDocs" :href "https://clojuredocs.org"}
   {:title "codewars" :href "https://www.codewars.com"}
   {:title "The Clojure Toolbox" :href "https://www.clojure-toolbox.com/"}
   {:title "Clojurescript" :href "https://www.clojurescript.org"}
   {:title "React" :href "https://reactjs.org/"}
   {:title "reagent" :href "https://reagent-project.github.io/"}
   {:title "re-frame" :href "https://github.com/Day8/re-frame"}
   {:title "Figwheel Main" :href "https://figwheel.org/"}])

(def contact-data
  [{:title "github" :icon (r/as-element [:> antd-icons/GithubFilled]) :href "https://github.com/jiesoul"}
   {:title "weibo" :icon (r/as-element [:> antd-icons/WeiboCircleFilled]) :href "https://weibo.com/jiesoul"}
   {:title "twitter" :icon (r/as-element [:> antd-icons/TwitterCircleFilled]) :http "https://twitter.com/jiesoul1982"}])
