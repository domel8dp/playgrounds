# Run NATS cluster

```bash
docker pull nats
# -DV is optional - for debugging
docker run --rm --name nats-1 nats -c nats-server.conf -DV
docker run --rm --name nats-2 --link nats-1 nats -c nats-server.conf --routes=nats-route://ruser:T0pS3cr3t@nats-1:6222 -DV
```

you can stop each container to test how the clients behave.

# Run Server and Client

The simplest way is to run Groovy scripts from yor IDE.

You can run multiple `NatsServer`'s in parallel.
