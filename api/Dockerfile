# builder
FROM clojure AS build
RUN mkdir -p /code
WORKDIR /code
COPY project.clj .
RUN lein deps

COPY env ./env
COPY resources ./resources
COPY src ./src
RUN lein uberjar


# 基础依赖
FROM openjdk:11-jre-slim
# 个人信息
MAINTAINER jiesoul <jiesoul@gmail.com>

WORKDIR /app

# 项目 jar 复制到容器下
COPY --from=build /code/target/soul-talk.jar .
# 复制启动脚本
COPY entrypoint.sh .
# 给启动脚本加可执行权限
RUN chmod +x ./entrypoint.sh

# 端口
EXPOSE 3000

# 入口
ENTRYPOINT [ "/app/entrypoint.sh" ]
# 运行命令
CMD [ "soul-talk-api"]