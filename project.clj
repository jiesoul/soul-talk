(defproject soul-talk "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.339"]
                 [org.clojure/java.jdbc "0.7.8"]
                 [org.postgresql/postgresql "42.2.4"]
                 [ragtime "0.7.2"]
                 [ring "1.6.3"]
                 [compojure "1.6.1"]
                 [com.taoensso/timbre "4.10.0"]
                 [com.fzakaria/slf4j-timbre "0.3.12"]
                 [metosin/ring-http-response "0.9.0"]
                 [ring/ring-defaults "0.3.2"]
                 [ring-middleware-format "0.7.2"]
                 [bouncer "1.0.1"]
                 [buddy "2.0.0"]
                 [clj-time "0.14.4"]
                 [joda-time "2.9.7"]
                 [cheshire "5.8.0"]
                 [selmer "1.12.0"]
                 [ring-webjars "0.2.0"]
                 [org.webjars/jquery "3.3.1-1"]
                 [org.webjars/bootstrap "4.1.3"]
                 [org.webjars/popper.js "1.14.1"]
                 [org.webjars/font-awesome "5.2.0"]
                 [domina "1.0.3"]
                 [reagent "0.8.1"]
                 [reagent-utils "0.3.1"]
                 [cljs-ajax "0.7.4"]]
  :main ^:skip-aot soul-talk.core
  :plugins [[lein-ring "0.12.4"]
            [lein-cljsbuild "1.1.7" :excludes [[org.clojure/clojure]]]
            [lein-figwheel "0.5.17-SNAPSHOT"]]
  :ring {:handler soul-talk.core/app}
  :source-paths ["src/clj" "src/cljc"]
  :resource-paths ["resources"]

  :clean-targets                                            ;; 清理临时文件
  ^{:protect false}
  [:target-path
   [:cljsbuild :builds :dev :compiler :output-dir]
   [:cljsbuild :builds :dev :compiler :output-to]]

  :cljsbuild
  {:builds {:dev                             ;; 开发配置
            {:source-paths ["src/cljs" "src/cljc"] ;; 源代码目录
             ;:resource-paths ["target/cljsbuild"]
             :figwheel     true                             ;; 开启 figwheel
             :compiler     {:main                 soul-talk.core ;; 主命名空间
                            :asset-path           "js/out"  ;; 加载文件的地方 和 临时目录相关
                            :output-to            "resources/public/js/main.js" ;; 主文件地方
                            :output-dir           "resources/public/js/out" ;; 临时文件目录
                            :optimizations :none
                            :source-map true      ;; 源代码
                            :pretty-print         true}}
            :prod
            {:source-paths ["src/cljs"]
             :compiler {:output-to "resources/public/js/main.js"
                        :optimizations :advanced
                        :pretty-print false}}}}  ;; 打印格式
  :figwheel
  {:css-dirs ["resources/public/css"]}
  :profiles {:dev {:source-paths ["env/dev/clj"]
                   :dependencies [[ring/ring-devel "1.6.3"]]}}
  )