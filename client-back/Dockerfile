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

# set work
WORKDIR /code

# update
RUN apt update

# curl
RUN apt-get install -y curl

# install node yarn
#RUN curl -fsSL https://deb.nodesource.com/setup_15.x | bash -
RUN apt-get install -y nodejs
RUN curl -qL https://www.npmjs.com/install.sh | sh
RUN npm install -g yarn

RUN ls -a .

# node build
#RUN npm install
RUN yarn install

# clojure build
RUN clj -A:prod


FROM nginx:alpine

LABEL creater="jiesoul" description="网站后台管理" version="1.0"

MAINTAINER jiesoul <jiesoul@gmail.com>

RUN mkdir /app

COPY --from=build  /code/resources/public/ /app/

ADD /admin.jiesoul.com.conf /etc/nginx/conf.d/

EXPOSE 8080



