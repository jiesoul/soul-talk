#!/bin/sh

if [ "$1" = 'soul-talk-api' ] ; then
    exec java -jar /app/soul-talk.jar "$@"
fi

exec "$@"