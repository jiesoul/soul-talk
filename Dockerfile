# 基础依赖
FROM java:8-alpine
# 个人信息
MAINTAINER jiesoul <jiesoul@gmail.com>

# 项目 jar 复制到容器下
ADD target/soul-talk.jar /app/soul-talk.jar
# 复制启动脚本
COPY entrypoint.sh /app/entrypoint.sh
# 给启动脚本加可执行权限
RUN chmod +x /app/entrypoint.sh

# 端口
EXPOSE 3000

# 入口
ENTRYPOINT [ "/app/entrypoint.sh" ]
# 运行命令
CMD [ "soul-talk" ]