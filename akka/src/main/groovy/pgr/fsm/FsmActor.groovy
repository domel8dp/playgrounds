package pgr.fsm

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.Behaviors

class FsmActor {

    interface Command { }
    static class MoveToNext implements Command {
        ActorRef<Response> replyTo
    }
    static class GetState implements Command {
        ActorRef<Response> replyTo
    }

    interface Response { }
    static class State implements Response {
        def state
    }
    enum Done implements Response { OK, FINAL }

    static Behavior<Command> create() {
        return init()
    }

    private static Behavior<Command> init() {
        return Behaviors.receive(Command)
                .onMessage(GetState, msg -> onGetState(msg, "init"))
                .onMessage(MoveToNext, msg -> {
                    msg.replyTo.tell(Done.OK)
                    next()
                })
                .build()
    }

    private static Behavior<Command> next() {
        return Behaviors.receive(Command)
                .onMessage(GetState, msg -> onGetState(msg, "next"))
                .onMessage(MoveToNext, msg -> {
                    msg.replyTo.tell(Done.OK)
                    finalState()
                })
                .build()
    }

    private static Behavior<Command> finalState() {
        return Behaviors.receive(Command)
                .onMessage(GetState, msg -> onGetState(msg, "final"))
                .onMessage(MoveToNext, msg -> {
                    msg.replyTo.tell(Done.FINAL)
                    return Behaviors.empty()
                })
                .build()
    }

    private static Behavior<Command> onGetState(GetState msg, String name) {
        msg.replyTo.tell(new State(state: name))
        return Behaviors.same()
    }
}
