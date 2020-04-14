package pgr.fsm

import akka.actor.typed.Behavior
import akka.actor.typed.SupervisorStrategy
import akka.actor.typed.Terminated
import akka.actor.typed.javadsl.*
import pgr.simple.EchoActor
import pgr.supervision.EscalatingParent
import pgr.supervision.Parent

import java.time.Duration

class Guardian extends AbstractBehavior<Command> {

    interface Command { }
    enum MoveToNext implements Command { INSTANCE }
    private static class State implements Command {
        def state
    }
    private enum Shutdown implements Command { INSTANCE }

    static Behavior<Command> create() {
        return Behaviors.<Command>setup(Guardian::new)
    }

    final def fsmActor
    final def stateResponseAdapter
    final def nextResponseAdapter

    private Guardian(ActorContext<Command> context) {
        super(context)
        fsmActor = context.spawn(FsmActor.create(), "fsm")
        stateResponseAdapter = context.messageAdapter(FsmActor.State, msg -> new State(state: msg.state))
        nextResponseAdapter = context.messageAdapter(FsmActor.Done, msg -> {
            return msg == FsmActor.Done.OK ? MoveToNext.INSTANCE : Shutdown.INSTANCE
        })
    }

    @Override
    Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(MoveToNext, notUsed -> onMoveToNext())
                .onMessage(State, this::onState)
                .onMessage(Shutdown, notUsed -> onShutdown())
                .build()
    }

    private Behavior<Command> onMoveToNext() {
        context.log.info "Received MoveToNext"
        fsmActor.tell(new FsmActor.GetState(replyTo: stateResponseAdapter))
        return this
    }

    private Behavior<Command> onState(State msg) {
        context.log.info "Received State: ${msg.state}"
        fsmActor.tell(new FsmActor.MoveToNext(replyTo: nextResponseAdapter))
        return this
    }

    private Behavior<Command> onShutdown() {
        context.log.info "Stopping system"
        context.system.terminate()
        return this
    }
}
