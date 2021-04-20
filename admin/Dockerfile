# clojure-build
FROM clojure:openjdk-11-tools-deps-slim-buster AS build

# make dir
RUN mkdir /code

# copy files
COPY package.json /code
COPY env /code/env
COPY src /code/src
COPY deps.edn /code
COPY prod.cljs.edn /code

# set work
WORKDIR /code

# update
RUN apt update

# install node yarn
RUN apt-get install -y nodejs npm
RUN npm install -g yarn

# node build
#RUN npm install
RUN yarn install

# clojure build
RUN clj -A:prod


FROM nginx

MAINTAINER jiesoul <jiesoul@gmail.com>

WORKDIR /app

COPY resources/public/css /app/css
COPY resources/public/images /app/images
COPY resources/public/index.html /app/index.html
COPY --from=build  /code/js/main_bundle.js /app/js/main_bundle.js

COPY admin.jiesoul.com.conf /etc/nginx/conf.d/default.conf

EXPOSE 80



