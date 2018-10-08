FROM clojure
COPY . /usr/app
WORKDIR /usr/app
CMD ["lein", "run"]

