package pgr.supervision

import akka.actor.typed.Behavior
import akka.actor.typed.PostStop
import akka.actor.typed.PreRestart
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive

class Child extends AbstractBehavior<Command> {

    interface Command { }
    enum Crash implements Command { INSTANCE }

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
            .onMessage(Crash, notUsed -> onCrash())
            .onSignal(PreRestart, notUsed -> onPreRestart())
            .onSignal(PostStop, notUsed -> onPostStop())
            .build()
    }

    private Behavior<Command> onCrash() {
        context.log.info "Crash received"
        throw new NumberFormatException("Child Crash")
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
