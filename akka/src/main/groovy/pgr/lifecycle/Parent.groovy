package pgr.lifecycle

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.PostStop
import akka.actor.typed.Terminated
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive

class Parent extends AbstractBehavior<Command> {

    interface Command { }
    enum Action implements Command { STOP_CHILD, STOP_CHILD_BY_MSG, STOP_CHILD_BY_MSG_WITH_CUSTOM, CRASH_CHILD, STOP, CRASH }
    interface Response { }
    enum Done implements Response { INSTANCE }

    static Behavior<Command> create(ActorRef<Response> replyTo) {
        return Behaviors.<Command>setup(ctx -> new Parent(ctx, replyTo)) as Behavior<Command>
    }

    final ActorRef childActor
    final def replyTo

    private Parent(ActorContext<Command> context, ActorRef<Response> replyTo) {
        super(context)
        this.replyTo = replyTo
        context.log.info "Starting Parent"
        childActor = context.spawn(Child.create(), "child")
        context.watch(childActor)
    }

    @Override
    Receive<Command> createReceive() {
        return newReceiveBuilder()
            .onMessage(Action, this::onAction)
            .onSignal(Terminated, this::onTerminated)
            .onSignal(PostStop, notUsed -> onPostStop())
            .build()
    }

    private Behavior<Command> onAction(Action msg) {
        context.log.info "Received msg: ${msg}"
        switch (msg) {
            case Action.STOP_CHILD:
                context.stop(childActor)
                return this
            case Action.STOP_CHILD_BY_MSG:
                childActor.tell(Child.Action.STOP_BY_MSG)
                return this
            case Action.STOP_CHILD_BY_MSG_WITH_CUSTOM:
                childActor.tell(Child.Action.STOP_BY_MSG_WITH_CUSTOM)
                return this
            case Action.CRASH_CHILD:
                childActor.tell(Child.Action.CRASH)
                return this
            case Action.STOP:
                return Behaviors.stopped()
            case Action.CRASH:
                throw new NumberFormatException("Parent Crash")
            default:
                context.log.info "Ignoring unknown action: ${msg}"
                return this
        }
    }

    private Behavior<Command> onTerminated(Terminated msg) {
        context.log.info "Received terminted: ${msg}"
        replyTo.tell(Done.INSTANCE)
        return this
    }

    private Behavior<Command> onPostStop() {
        context.log.info "PostStop signal received"
        return this
    }
}
