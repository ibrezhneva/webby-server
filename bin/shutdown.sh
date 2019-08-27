#!/bin/sh

# -----------------------------------------------------------------------------
# Stop script for the Webby Server
# -----------------------------------------------------------------------------

set -e
if pid=$(cat webby.pid)
then
    echo "Shutting down Webby Server..."
    kill -15 $pid
    rm webby.pid
    echo "Server stopped"
else
    echo "Webby Server is not running. Stop aborted."
fi
exit 0
