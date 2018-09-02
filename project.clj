(defproject soul-talk "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.238"]
                 [ring "1.6.3"]
                 [compojure "1.6.1"]
                 [metosin/ring-http-response "0.9.0"]
                 [ring/ring-defaults "0.3.2"]
                 [selmer "1.12.0"]
                 [ring-webjars "0.2.0"]
                 [org.webjars/jquery "3.3.1-1"]
                 [org.webjars/bootstrap "4.1.3"]
                 [org.webjars/popper.js "1.14.1"]
                 [org.webjars/font-awesome "5.2.0"]
                 [domina "1.0.3"]]
  :main ^:skip-aot soul-talk.core
  :plugins [[lein-ring "0.12.4"]
            [lein-cljsbuild "1.1.7" :excludes [[org.clojure/clojure]]]
            [lein-figwheel "0.5.17-SNAPSHOT"]]
  :ring {:handler soul-talk.core/app}
  :source-paths ["src"]
  :resource-paths ["resources"]

  :clean-targets                                            ;; 清理临时文件
  ^{:protect false}
  [:target-path
   [:cljsbuild :builds :dev :compiler :output-dir]
   [:cljsbuild :builds :dev :compiler :output-to]]

  :cljsbuild
  {:builds {:dev                             ;; 开发配置
            {:source-paths ["src-cljs"]                     ;; 源代码目录
             :figwheel     true                             ;; 开启 figwheel
             :compiler     {
                            :main                 soul-talk.core ;; 主命名空间
                            :asset-path           "js/out"  ;; 加载文件的地方 和 临时目录相关
                            :output-to            "resources/public/js/main.js" ;; 主文件地方
                            :output-dir           "resources/public/js/out" ;; 临时文件目录
                            :optimizations :none
                            :source-map-timestamp true      ;; 源代码
                            :pretty-print         true}}

            :prod
            {:source-paths ["src-cljs"]
             :compiler {:output-to "resources/public/js/main.js"
                        :optimizations :advanced
                        :pretty-print false}}}}  ;; 打印格式
  :figwheel
  {:css-dirs ["resources/public/css"]})