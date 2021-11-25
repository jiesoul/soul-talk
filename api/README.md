# soul-talk-api

## Postgresql

###  安装 postgresql (Ubuntu 20.04) 创建用户和数据库
   
```bash
sudo sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'

wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -

sudo apt-get update

sudo apt-get -y install postgresql

# 切换postgresql 数据库用户
su - postgres

# 进入 psql
psql

CREATE USER dbuser WITH PASSWORD '*****';

CREATE DATABASE exampledb OWNER dbuser;

GRANT ALL PRIVILEGES ON DATABASE exampledb TO dbuser;

# 退出 psql
\q 
```
#### 开启远程访问

>1、编辑配置文件 
> 
> 文件：postgresql.conf
> 
> 位置：/var/lib/pgsql/data/postgresql.conf
>
> 添加/修改：在所有IP地址上监听，从而允许远程连接到数据库服务器：
>
> listening_address: '*'

> 文件：pg_hba.conf
>
> 位置：/var/lib/pgsql/data/pg_hba.conf
>
> 添加/修改：允许任意用户从任意机器上以密码方式访问数据库，把下行添加为第一条规则：
> 
> host    all             all             0.0.0.0/0               md5

#### 重启数据库
```bash
sudo systemctl restart postgresql
```

## docker 模式 postgresql
```docker
docker run -it --name pg-dev -p 5433:5432 -e POSTGRES_USER=jiesoul -e POSTGRES_PASSWORD=12345678 -e POSTGRES_DB=soul_talk -d postgres
```

## 开发模式

```bash
lein repl 

# user=>
(dev)

(go)
```

## 生产模式

```bash
# 打包
lein uberjar

# docker 构建
docker build -d xxx/xxxx:latest .

# 推送 docker 镜像
docker pull xxx/xxxx:latest

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