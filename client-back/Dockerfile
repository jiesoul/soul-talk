# clojure-build
FROM clojure:openjdk-11-tools-deps-slim-buster AS build

# make dir
RUN mkdir /code

# copy files
COPY package.json /code
COPY env /code/env
COPY resources /code/resource
COPY src /code/src
COPY deps.edn /code
COPY prod.cljs.edn /code

# install node yarn
RUN apt-get install -y node yarn

# set work
WORKDIR /code

# node build
RUN yarn install

# clojure build
RUN clj -A:prod


FROM nginx

MAINTAINER jiesoul <jiesoul@gmail.com>

RUN mkdir /app

COPY --from=build  /code/resources/public/ /app/

EXPOSE 8080



