#!/usr/bin/env bash
# wait-for-it.sh

set -e

hostport="$1"
shift
cmd="$@"

host=$(echo $hostport | cut -d: -f1)
port=$(echo $hostport | cut -d: -f2)

echo "Waiting for $host:$port to be available..."

until nc -z "$host" "$port"; do
  sleep 2
done

echo "$host:$port is available, starting app..."
exec $cmd
