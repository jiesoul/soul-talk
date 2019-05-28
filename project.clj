(defproject soul-talk "0.1.4"
  :description "self site"
  :url "http://github.com/jiesoul/soul-talk"

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [seancorfield/next.jdbc "1.0.0-beta1"]
                 [org.clojure/java.jdbc "0.7.8"]
                 [org.postgresql/postgresql "42.2.4"]
                 [org.clojure/tools.cli "0.3.1"]
                 [hikari-cp "2.6.0"]
                 [ragtime "0.7.2"]
                 [ring "1.7.1"]
                 [mount "0.1.13"]
                 [tolitius/mount-up "0.1.1"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [compojure "1.6.1"]
                 [metosin/compojure-api "2.0.0-alpha28"]
                 [expound "0.7.1"]
                 [metosin/spec-tools "0.8.2"]
                 [metosin/ring-http-response "0.9.0"]
                 [metosin/muuntaja "0.6.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [ring/ring-defaults "0.3.2"]
                 [ring-cors "0.1.13"]
                 [toucan "1.12.0"]
                 [bouncer "1.0.1"]
                 [buddy "2.0.0"]
                 [clojure.java-time "0.3.2"]
                 [cheshire "5.8.0"]
                 [cprop "0.1.13"]]

  :main ^:skip-aot soul-talk.core

  :plugins [[lein-ring "0.12.4"]
            [io.sarnowski/lein-docker "1.1.0"]]

  :ring {:handler soul-talk.handler/app}

  :source-paths ["src/clj" "src/cljc"]
  :resource-paths ["resources"]
  :test-paths ["test/clj"]

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
       :dependencies   [[ring/ring-devel "1.6.3"]
                        [ring/ring-mock "0.3.2"]
                        [pjstadig/humane-test-output "0.8.3"]
                        [com.cemerick/piggieback "0.2.2"]
                        [org.clojure/tools.nrepl "0.2.13"]
                        [org.clojure/test.check "0.9.0"]]
       :plugins        [[com.jakemccrary/lein-test-refresh "0.23.0"]]}})
