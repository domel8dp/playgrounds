package pgr.lifecycle

import akka.actor.typed.Behavior
import akka.actor.typed.Terminated
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive

class Guardian extends AbstractBehavior<Command> {

    interface Command { }
    enum Action implements Command { STOP_CHILD, STOP_CHILD_BY_MSG, STOP_CHILD_BY_MSG_WITH_CUSTOM, CRASH_CHILD, STOP, CRASH }
    private enum Shutdown implements Command { INSTANCE }

    static Behavior<Command> create() {
        return Behaviors.setup(Guardian::new) as Behavior<Command>
    }

    final def parentActor
    final def parentResponseAdapter

    private Guardian(ActorContext<Command> context) {
        super(context)
        parentResponseAdapter = context.messageAdapter(Parent.Response, notUsed -> Shutdown.INSTANCE)
        parentActor = context.spawn(Parent.create(parentResponseAdapter), "parent")
        context.watch(parentActor)
    }

    @Override
    Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(Action, this::onAction)
                .onMessage(Shutdown, notUsed -> onShutdown())
                .onSignal(Terminated, this::onTerminated)
                .build()
    }

    private Behavior<Command> onAction(Action msg) {
        context.log.info "Starting ${msg} scenario"
        def action = map(msg)
        if (action) {
            parentActor.tell(action)
            return this
        } else {
                context.log.info "Ignoring unknown scenario: ${msg}"
                return this
        }
    }

    private Parent.Action map(Action msg) {
        switch (msg) {
            case Action.STOP_CHILD: return Parent.Action.STOP_CHILD
            case Action.STOP_CHILD_BY_MSG: return Parent.Action.STOP_CHILD_BY_MSG
            case Action.STOP_CHILD_BY_MSG_WITH_CUSTOM: return Parent.Action.STOP_CHILD_BY_MSG_WITH_CUSTOM
            case Action.CRASH_CHILD: return Parent.Action.CRASH_CHILD
            case Action.STOP: return Parent.Action.STOP
            case Action.CRASH: return Parent.Action.CRASH
            default: return null
        }
    }

    private Behavior<Command> onShutdown() {
        context.log.info "Stopping system"
        context.system.terminate()
        return this
    }

    private Behavior<Command> onTerminated(Terminated msg) {
        context.log.info "Received terminted: ${msg}"
        context.system.terminate()
        return this
    }
}
