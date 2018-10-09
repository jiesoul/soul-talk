FROM java:8-alpine
MAINTAINER jiesoul <jiesoul@gmail.com>

ADD target/soul-talk.jar /app/soul-talk.jar
EXPOSE 3000

CMD ["java", "-jar", "/app/soul-talk.jar"]

