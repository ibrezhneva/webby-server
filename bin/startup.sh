#!/bin/sh

# -----------------------------------------------------------------------------
# Start Script for the Webby Server
# -----------------------------------------------------------------------------

if pid=$(pgrep -f webby-server-1.0-SNAPSHOT.jar)
then    
    echo "Webby Server is already running. PID: $$"    
else
    cd ..
    nohup java -jar webby-server-1.0-SNAPSHOT.jar > logs/webby_log.log 2>&1 &
    echo "Webby Server started."
fi
exit 0
