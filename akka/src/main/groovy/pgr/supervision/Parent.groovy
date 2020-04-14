package pgr.supervision

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.PostStop
import akka.actor.typed.PreRestart
import akka.actor.typed.SupervisorStrategy
import akka.actor.typed.Terminated
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive

class Parent extends AbstractBehavior<Command> {

    interface Command { }
    enum Action implements Command { CRASH_CHILD, CRASH }

    static Behavior<Command> create() {
        return Behaviors.<Command>setup(Parent::new) as Behavior<Command>
    }

    final ActorRef childActor

    private Parent(ActorContext<Command> context) {
        super(context)
        context.log.info "Starting Parent"
        def childBehavior = Behaviors.supervise(Child.create())
                .onFailure(SupervisorStrategy.restart())
        childActor = context.spawn(childBehavior, "child")
        context.watch(childActor)
    }

    @Override
    Receive<Command> createReceive() {
        return newReceiveBuilder()
            .onMessage(Action, this::onAction)
            .onSignal(Terminated, this::onTerminated)
            .onSignal(PreRestart, notUsed -> onPreRestart())
            .onSignal(PostStop, notUsed -> onPostStop())
            .build()
    }

    private Behavior<Command> onAction(Action msg) {
        context.log.info "Received msg: ${msg}"
        switch (msg) {
            case Action.CRASH_CHILD:
                childActor.tell(Child.Crash.INSTANCE)
                return this
            case Action.CRASH:
                throw new NumberFormatException("Parent Crash")
            default:
                context.log.info "Ignoring unknown action: ${msg}"
                return this
        }
    }

    private Behavior<Command> onTerminated(Terminated msg) {
        context.log.info "Received terminted: ${msg}"
        return this
    }

    private Behavior<Command> onPreRestart() {
        context.log.info "PreRestart signal received"
        return this
    }

    private Behavior<Command> onPostStop() {
        context.log.info "PostStop signal received"
        return this
    }
}
