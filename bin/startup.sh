#!/bin/sh

# -----------------------------------------------------------------------------
# Start Script for the Webby Server
# -----------------------------------------------------------------------------

set -e
if [ -f webby.pid ]
then
    pid=$(cat webby.pid)
    echo "Webby Server is already running. PID: $pid"
else
    cd ..
    nohup java -jar webby-server-1.0-SNAPSHOT.jar > logs/webby_log.log 2>&1 &
    echo $! > bin/webby.pid
    echo "Webby Server started."
fi
exit 0
