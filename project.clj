(defproject soul-talk "0.1.4"
  :description "self site"
  :url "http://github.com/jiesoul/soul-talk"

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.520"]
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
                 [selmer "1.12.0"]
                 [ring-webjars "0.2.0"]
                 [org.webjars/jquery "3.3.1-1"]
                 [org.webjars/bootstrap "4.1.3"]
                 [org.webjars/popper.js "1.14.1"]
                 [org.webjars/font-awesome "4.7.0"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]
                 [cljsjs/showdown "1.8.6-0"]
                 [baking-soda "0.2.0"]
                 [reagent "0.8.1" :exclusions [cljsjs/react
                                               cljsjs/react-dom]]
                 [cljsjs/react "16.6.0-0"]
                 [cljsjs/react-dom "16.6.0-0"]
                 [cljsjs/react-transition-group "2.4.0-0"]
                 [cljsjs/react-popper "0.10.4-0"]
                 [cljsjs/reactstrap "6.0.1-0"]
                 [cljsjs/moment "2.24.0-0"]
                 [cljsjs/antd "3.16.4-0"]
                 [cljsjs/ant-design-pro "2.1.1-0"]
                 [com.hypaer/ant-man "1.7.4"]
                 [org.clojars.frozenlock/reagent-modals "0.2.8"]
                 [secretary "1.2.3"]
                 [re-frame "0.10.6"]
                 [re-com "2.2.0"]
                 [venantius/accountant "0.2.4"]
                 [cljs-ajax "0.7.4"]
                 [cprop "0.1.13"]]

  :main ^:skip-aot soul-talk.core

  :plugins [[lein-ring "0.12.4"]
            [lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.8"]
            [io.sarnowski/lein-docker "1.1.0"]]

  :ring {:handler soul-talk.handler/app}

  :source-paths ["src/clj" "src/cljc"]
  :resource-paths ["resources"]
  :test-paths ["test/clj" "test/cljs"]


  :clean-targets                                            ;; 清理临时文件
  ^{:protect false}
  [:target-path
   [:cljsbuild :builds :dev :compiler :output-dir]
   [:cljsbuild :builds :dev :compiler :output-to]]

  :figwheel
  {:http-server-root "public"
   :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]
   :css-dirs         ["resources/public/css"]}

  ;:aliases
  ;{"fig" ["trampoline" "run" "-m" "figwheel.main"]
  ; "build" ["trampoline" "run" "-m" "figwheel.main" "-b" "boodle"]}

  :docker {:image-name "jiesoul/soul-talk"}

:profiles
{:uberjar
      {:omit-source    true
       :prep-tasks     ["compile" ["cljsbuild" "once" "prod"]]
       :cljsbuild
                       {:builds
                        {:prod
                         {:source-paths ["src/cljs" "src/cljc" "env/prod/cljs"]
                          :compiler     {:output-to        "resources/public/js/main.js"
                                         :externs          ["react/externs/react.js"
                                                            "public/jslib/simplemde.min.js"
                                                            "public/jslib/highlight.js"
                                                            "public/jslib/codemirror.js"]
                                         :closure-warnings {:externs-validation :off
                                                            :non-standard-jsdoc :off}
                                         :optimizations    :advanced
                                         :pretty-print     false}}}}
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
                        [figwheel-sidecar "0.5.16"]
                        ;[com.bhauman/figwheel-main "0.2.0"]
                        ;[com.bhauman/rebel-readline-cljs "0.1.4"]
                        [binaryage/devtools "0.9.10"]
                        [re-frisk "0.5.4"]
                        [devcards "0.2.5"]
                        [doo "0.1.10"]
                        [com.cemerick/piggieback "0.2.2"]
                        [org.clojure/tools.nrepl "0.2.13"]
                        [org.clojure/test.check "0.9.0"]
                        [day8.re-frame/re-frame-10x "0.3.3-react16"]]
       :plugins        [[com.jakemccrary/lein-test-refresh "0.23.0"]
                        [lein-doo "0.1.10"]]
       :cljsbuild
                       {:builds
                        {:dev {
                               :source-paths ["src/cljs" "src/cljc" "env/dev/cljs"] ;; 源代码目录
                               :compiler     {:main          "soul-talk.app" ;; 主命名空间
                                              :asset-path    "/js/out" ;; 加载文件的地方 和 临时目录相关
                                              :output-to     "resources/public/js/main.js" ;; 主文件地方
                                              :output-dir    "resources/public/js/out" ;; 临时文件目录
                                              :optimizations :none
                                              :source-map    true ;; 源代码
                                              :pretty-print  true
                                              :preloads      [re-frisk.preload]}}
                         :test
                              {
                               :figwheel     {:devcards true}
                               :source-paths ["src/cljs" "src/cljc" "test/cljs"]
                               :compiler     {:output-to     "target/test.js"
                                              :main          soul-talk.runner
                                              :optimizations :none}}}}}})
