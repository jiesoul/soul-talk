(defproject soul-talk "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.339"]
                 [org.clojure/java.jdbc "0.7.8"]
                 [org.postgresql/postgresql "42.2.4"]
                 [hikari-cp "2.6.0"]
                 [ragtime "0.7.2"]
                 [ring "1.6.3"]
                 [mount "0.1.13"]
                 [org.clojure/tools.namespace "0.3.0-alpha4"]
                 [compojure "1.6.1"]
                 [metosin/compojure-api "2.0.0-alpha25"]
                 [metosin/spec-tools "0.7.1"]
                 [com.taoensso/timbre "4.10.0"]
                 [metosin/ring-http-response "0.9.0"]
                 [ring/ring-defaults "0.3.2"]
                 [metosin/muuntaja "0.6.0"]
                 [bouncer "1.0.1"]
                 [buddy "2.0.0"]
                 [clojure.java-time "0.3.2"]
                 [cheshire "5.8.0"]
                 [selmer "1.12.0"]
                 [ring-webjars "0.2.0"]
                 [org.webjars/jquery "3.3.1-1"]
                 [org.webjars/bootstrap "4.1.3"]
                 [org.webjars/popper.js "1.14.1"]
                 [org.webjars/font-awesome "5.2.0"]
                 [cljsjs/chartjs "2.7.0-0"]
                 [domina "1.0.3"]
                 [reagent "0.8.1"]
                 [secretary "1.2.3"]
                 [re-frame "0.10.6"]
                 [venantius/accountant "0.2.4"]
                 [reagent-utils "0.3.1"]
                 [cljs-ajax "0.7.4"]
                 [cprop "0.1.13"]]
  :main ^:skip-aot soul-talk.core
  :plugins [[lein-ring "0.12.4"]
            [lein-cljsbuild "1.1.7"]]

  :ring {:handler soul-talk.core/app}
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
   :css-dirs ["resources/public/css"]}

  :profiles
  {:dev  {:source-paths ["env/dev/clj"]
          :resource-paths ["env/dev/resources"]
          :repl-options {:init-ns user}
          :dependencies [[ring/ring-devel "1.6.3"]
                         [ring/ring-mock "0.3.2"]
                         [pjstadig/humane-test-output "0.8.3"]
                         [figwheel-sidecar "0.5.16"]
                         [devcards "0.2.5"]
                         [doo "0.1.10"]
                         [com.cemerick/piggieback "0.2.2"]
                         [org.clojure/tools.nrepl "0.2.13"]
                         [org.clojure/test.check "0.9.0"]]
          :plugins      [[com.jakemccrary/lein-test-refresh "0.23.0"]
                         [lein-doo "0.1.10"]]
          :cljsbuild
                        {:builds
                         [{:id           "dev"
                           :source-paths ["src/cljs" "src/cljc"] ;; 源代码目录
                           ;:resource-paths ["target/cljsbuild"]
                           :figwheel     true               ;; 开启 figwheel
                           :compiler     {:main          soul-talk.core ;; 主命名空间
                                          :asset-path    "js/out" ;; 加载文件的地方 和 临时目录相关
                                          :output-to     "resources/public/js/main.js" ;; 主文件地方
                                          :output-dir    "resources/public/js/out" ;; 临时文件目录
                                          :optimizations :none
                                          :source-map    true ;; 源代码
                                          :pretty-print  true}}
                          {:id           "test"
                           :figwheel {:devcards true}
                           :source-paths ["src/cljs" "src/cljc" "test/cljs"]
                           :compiler     {:output-to     "target/test.js"
                                          :main          soul-talk.runner
                                          :optimizations :none}}
                          ]}}}

  )
