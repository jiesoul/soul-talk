# 基础依赖
FROM java:8-alpine
# 个人信息
MAINTAINER jiesoul <jiesoul@gmail.com>

# 项目 jar 复制到容器下
ADD target/soul-talk.jar /app/soul-talk.jar
#COPY entrypoint.sh /app/entrypoint.sh
#RUN chmod +x /app/entrypoint.sh

# 端口
EXPOSE 3000

# 运行命令
CMD ["java", "-jar", "/app/soul-talk.jar", "migrate"]
CMD ["java", "-jar", "/app/soul-talk.jar"]
#ENTRYPOINT [ "/app/entrypoint.sh" ]
#CMD [ "soul-talk" ]
