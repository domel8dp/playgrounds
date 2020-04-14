package pgr.supervision

import akka.actor.typed.*
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive

class EscalatingParent extends AbstractBehavior<Command> {

    interface Command { }
    enum CrashChild implements Command { INSTANCE }

    static Behavior<Command> create() {
        return Behaviors.<Command>setup(EscalatingParent::new)
    }

    final ActorRef childActor

    private EscalatingParent(ActorContext<Command> context) {
        super(context)
        context.log.info "Starting Escalating Parent"
        childActor = context.spawn(Child.create(), "child")
        context.watch(childActor)
    }

    @Override
    Receive<Command> createReceive() {
        return newReceiveBuilder()
            .onMessage(CrashChild, notUsed -> onCrashChild())
            .onSignal(PostStop, notUsed -> onPostStop())
            .build()
    }

    private Behavior<Command> onCrashChild() {
        context.log.info "Received CrashChild"
        childActor.tell(Child.Crash.INSTANCE)
        return this
    }

    private Behavior<Command> onPostStop() {
        context.log.info "PostStop signal received"
        return this
    }
}
