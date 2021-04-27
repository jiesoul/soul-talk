# soul-talk-server

* postgresql (Ubuntu 20.04)
   
```bash
sudo sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'

wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -

sudo apt-get update

sudo apt-get -y install postgresql 
```

dev mode 

```bash
lein repl 

# user=>
(dev)

(go)
```

prod mede
```bash
# 打包
lein uberjar

# 构建
lein docker build

# 提交到 docker hub
lein docker pushr

# 更新数据库
java -jar target/soul-talk.jar migrate

# 执行
java -jar target/soul-talk.jar
 ```


Docker Jenkins Issue
Make a new file if not existing..

# vim /etc/default/docker
ExecStart=/usr/bin/dockerd -H fd:// -H tcp://0.0.0.0:2375
Edit service file

# vim /lib/systemd/system/docker.service
ExecStart=/usr/bin/dockerd -H fd://                <--- before
ExecStart=/usr/bin/dockerd -H fd:// -H tcp://0.0.0.0:2375    <--- After
Add additional hosts for Jenkins. and reload & restart docker service

# systemctl daemon-reload
# systemctl restart docker