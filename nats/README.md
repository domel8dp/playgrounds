# Run NATS cluster

```bash
docker pull nats:2
# -DV is optional - for debugging
docker run --rm --name nats-1 nats:2 -c nats-server.conf --auth ready2go -DV
docker run --rm --name nats-2 --link nats-1 nats:2 -c nats-server.conf --auth ready2go --routes=nats-route://ruser:T0pS3cr3t@nats-1:6222 -DV
```

you can stop each container to test how the clients behave.

# Run Server and Client

The simplest way is to run Groovy scripts from yor IDE.

You can run multiple `NatsServer`'s in parallel.
