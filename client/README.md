# soul-talk-web

首先确保你安装了 java clojure nodejs yarn 

安装 NPM 依赖：
```bash
yarn install
```

更新所有 js 包
```base
yarn upgrade-interactive --latest
```

打包外部 JS
```bash
yarn webpack
```

开发运行
```bash
clojure -A:fig:dev
```

打包生产
```bash
clojure -A:fig:prod
```

打包生产并运行服务
```bash
clojure -A:fig:prod -s

## http://localhost:9500
```

