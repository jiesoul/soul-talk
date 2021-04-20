#!/bin/sh

if [ "$1" = 'soul-talk-api' ] ; then
    java -jar /app/soul-talk.jar migrate
    exec java -jar /app/soul-talk.jar "$@"
fi

exec "$@"