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
COPY resources/public /code/

# set work
WORKDIR /code

# update
RUN apt update

# install node yarn
#RUN curl -fsSL https://deb.nodesource.com/setup_16.x | bash -
#RUN apt-get install -y nodejs

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

COPY --from=build /code/css /app/css
COPY --from=build /code/images /app/images
COPY --from=build /code/index.html /app/index.html
COPY --from=build  /code/js/main_bundle.js /app/js/main_bundle.js

COPY admin.jiesoul.com.conf /etc/nginx/conf.d/default.conf

EXPOSE 80



