FROM nginx

MAINTAINER jiesoul <jiesoul@gmail.com>

RUN mkdir /app

COPY resources/public/ /app/

RUN mkdir /app/cljs

COPY target/public/cljs/app.js /app/cljs/app.js

EXPOSE 80



