#!/bin/sh

# -----------------------------------------------------------------------------
# Stop script for the Webby Server
# -----------------------------------------------------------------------------

if pid=$(pgrep -f webby-server-1.0-SNAPSHOT.jar)
then    
    echo "Shutting down Webby Server..."
    kill -15 $pid
    echo "Server stopped"
else
    echo "Webby Server is not running. Stop aborted."
fi
exit 0
