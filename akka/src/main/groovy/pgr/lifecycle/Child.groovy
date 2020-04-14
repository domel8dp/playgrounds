package pgr.lifecycle

import akka.actor.typed.Behavior
import akka.actor.typed.PostStop
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive

class Child extends AbstractBehavior<Command> {

    interface Command { }
    enum Action implements Command { STOP_BY_MSG, STOP_BY_MSG_WITH_CUSTOM, CRASH }

    static Behavior<Command> create() {
        return Behaviors.setup(Child::new) as Behavior<Command>
    }

    private Child(ActorContext<Command> context) {
        super(context)
        context.log.info "Starting Child"
    }

    @Override
    Receive<Command> createReceive() {
        return newReceiveBuilder()
            .onMessage(Action, this::onAction)
            .onSignal(PostStop, notUsed -> onPostStop())
            .build()
    }

    private Behavior<Command> onAction(Action msg) {
        context.log.info "Action received: ${msg}"
        switch (msg) {
            case Action.STOP_BY_MSG:
                return Behaviors.<Command>stopped()
            case Action.STOP_BY_MSG_WITH_CUSTOM:
                return Behaviors.<Command>stopped(this::customCleanUp)
            case Action.CRASH:
                throw new NumberFormatException("Child Crash")
            default:
                context.log.info "UNKNOWN Action"
                return this
        }
    }

    private void customCleanUp() {
        context.log.info "Custom StopByMsg cleanup"
    }

    private Behavior<Command> onPostStop() {
        context.log.info "PostStop signal received"
        return this
    }
}
