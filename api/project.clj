;; 服务商配置文件 使用 leiningen 作用运行
(defproject soul-talk-api "0.2"
  :description "self site api"
  :url "http://github.com/jiesoul/soul-talk/api"

  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/spec.alpha "0.3.214"]
                 [org.clojure/tools.cli "1.0.206"]
                 [org.clojure/tools.namespace "1.1.0"]

                 ;; 日志
                 [com.taoensso/timbre "5.1.2"]
                 [com.fzakaria/slf4j-timbre "0.3.21"]
                 [org.clojure/tools.logging "1.1.0"]
                 [ch.qos.logback/logback-classic "1.2.3"]

                 [clojure.java-time "0.3.2"]

                 ;; web api
                 [ring "1.9.4"]
                 [ring-cors "0.1.13"]
                 [ring/ring-defaults "0.3.3"]
                 [compojure "1.6.2"]
                 [metosin/compojure-api "2.0.0-alpha31"]
                 [metosin/ring-http-response "0.9.3"]
                 [metosin/ring-swagger "0.26.2"]
                 [metosin/ring-swagger-ui "4.0.0"]
                 [metosin/spec-tools "0.10.5"]
                 [metosin/muuntaja "0.6.8"]
                 [metosin/metosin-common "0.5.0"]

                 ;;database
                 [com.github.seancorfield/next.jdbc "1.2.753"]
                 [org.postgresql/postgresql "42.2.4"]
                 [com.zaxxer/HikariCP "3.4.5"]
                 [ragtime "0.8.1"]
                 [toucan "1.15.0"]

                 ;; Auth validation
                 [buddy "2.0.0"]
                 [bouncer "1.0.1"]

                 ;; DI
                 [mount "0.1.16"]
                 [tolitius/mount-up "0.1.3" :exclusions [ch.qos.logback/logback-classic]]

                 ;; formats clojure.spec error messages
                 [expound "0.8.10"]
                 [flake "0.4.5"]

                 ;;other
                 [environ "1.2.0"]
                 [cheshire "5.10.0"]
                 [cprop "0.1.19"]]


  :source-paths ["src"]
  :resource-paths ["resources"]
  :test-paths ["test"]
  :injections [(:require 'clojure.pprint)]
  :clean-targets [:target-path]

  :main ^:skip-aot soul-talk.core

:profiles
{:uberjar
      {:omit-source    true
       :aot            :all
       :main soul-talk.core
       :uberjar-name   "soul-talk.jar"
       :jar-exclusions [#"(?:^|/).git/"]
       :source-paths   ["env/prod/clj"]
       :resource-paths ["env/prod/resources"]}

 :dev {:source-paths   ["env/dev/clj"]
       :resource-paths ["env/dev/resources"]
       :repl-options   {:init-ns user}
       :middleware      [whidbey.plugin/repl-pprint]
       :dependencies   [[ring/ring-mock "0.3.2"]
                        [midje "1.7.0"]
                        [eftest "0.5.9"]
                        [org.clojure/test.check "1.1.0"]
                        [lein-ancient "0.6.15"]
                        [com.cemerick/piggieback "0.2.2"]
                        [org.clojure/tools.nrepl "0.2.13"]
                        [com.jakemccrary/lein-test-refresh "0.25.0"]
                        [org.clojure/test.check "1.1.0"]]
       :plugins        [[mvxcvi/whidbey "2.2.1"]
                        [lein-eftest "0.5.9"]
                        [com.jakemccrary/lein-test-refresh "0.24.1"]
                        [pjstadig/humane-test-output "0.10.0"]]}})
