#!/bin/sh

java -jar /app/soul-talk.jar migrate
exec java -jar /app/soul-talk.jar "$@"