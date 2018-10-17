#!/bin/sh

# 默认本地配置文件目录
if [ -z "$CONFIG_END" ] ;then
        CONFIG_END = /app/config.edn
fi

if [ "$1" = 'soul-talk' ] ; then
    java ${JAVA_OPTS} -Dconf=${CONFIG_END} -jar /app/soul-talk.jar migrate
    exec java ${JAVA_OPTS} -Dconf=${CONFIG_END} -jar /app/soul-talk.jar "$@"
fi

exec "$@"