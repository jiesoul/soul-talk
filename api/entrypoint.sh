#!/bin/sh

exec java -jar /app/soul-talk.jar migrate
#if [ "$1" = 'soul-talk-api' ] ; then
#
#fi
#
#exec "$@"