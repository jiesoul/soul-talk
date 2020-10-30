;; 服务商配置文件 使用 leiningen 作用运行
(defproject soul-talk "0.1.5"
  :description "self site"
  :url "http://github.com/jiesoul/soul-talk"

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.cli "0.3.1"]
                 [org.clojure/tools.namespace "0.2.11"]

                 ;; web api
                 [ring "1.8.1"]
                 [ring-cors "0.1.13"]
                 [ring/ring-defaults "0.3.2"]
                 [compojure "1.6.1"]
                 [metosin/compojure-api "2.0.0-alpha31"]
                 [metosin/ring-http-response "0.9.0"]
                 [metosin/ring-swagger-ui "3.36.0"]
                 [metosin/spec-tools "0.10.4"]
                 [metosin/muuntaja "0.6.0"]

                 ;;database
                 [seancorfield/next.jdbc "1.1.610"]
                 [org.postgresql/postgresql "42.2.4"]
                 [com.zaxxer/HikariCP "3.4.5"]
                 [ragtime "0.8.0"]

                 ;; Auth validation
                 [buddy "2.0.0"]
                 [bouncer "1.0.1"]

                 ;; DI
                 [mount "0.1.13"]
                 [tolitius/mount-up "0.1.1"]

                 [expound "0.7.1"]
                 [com.taoensso/timbre "4.10.0"]
                 [toucan "1.12.0"]
                 [clojure.java-time "0.3.2"]
                 [environ "1.2.0"]
                 [cheshire "5.10.0"]
                 [cprop "0.1.13"]]

  :main ^:skip-aot soul-talk.core

  :plugins [[lein-ring "0.12.4"]
            [io.sarnowski/lein-docker "1.1.0"]]

  :ring {:handler soul-talk.handler/app}

  :source-paths ["src"]
  :resource-paths ["resources"]
  :test-paths ["test"]

  :docker {:image-name "jiesoul/soul-talk"}

:profiles
{:uberjar
      {:omit-source    true
       :aot            :all
       :uberjar-name   "soul-talk.jar"
       :source-paths   ["env/prod/clj"]
       :resource-paths ["env/prod/resources"]}

 :dev {:source-paths   ["env/dev/clj"]
       :resource-paths ["env/dev/resources"]
       :repl-options   {:init-ns user}
       :middleware      [whidbey.plugin/repl-pprint]
       :dependencies   [[ring/ring-devel "1.6.3"]
                        [ring/ring-mock "0.3.2"]
                        [midje "1.7.0"]
                        [com.jakemccrary/lein-test-refresh "0.24.1"]
                        [com.cemerick/piggieback "0.2.2"]
                        [org.clojure/tools.nrepl "0.2.13"]
                        [org.clojure/test.check "1.1.0"]]
       :injections [(require 'pjstadig.humane-test-output)
                    (pjstadig.humane-test-output/activate!)]
       :plugins        [[mvxcvi/whidbey "2.2.1"]
                        [venantius/ultra "0.6.0"]
                        ;[pjstadig/humane-test-output "0.10.0"]
                        ]}})
