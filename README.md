# soul-talk

A Clojure library designed to ... well, that part is up to you.

* api 网站api 服务
* admin 网站管理后台
* home 网站前端

## Usage

```bash
docker build -t api:latest ./api

docker build -t admin:latest ./admin

docker build -t home:latest ./home

docker swarn init

docker stack deploy -c docker-compose.yml soultalk
```

FIXME

## License

Copyright © 2018 FIXME

MIT License

nwKjha7K4ig8sqBa+F78I7BvMYlGrw5vcsOMBywgMxE