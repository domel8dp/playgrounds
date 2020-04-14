package pgr.simple

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive

import java.util.concurrent.CompletableFuture

class EchoActor extends AbstractBehavior<Command> {

    interface Command { }
    static class Msg implements Command {
        String text
        ActorRef<Response> replyTo
    }
    private static class FutureResponse implements Command {
        ActorRef<Response> replyTo
    }
    interface Response { }
    enum Done implements Response { INSTANCE }

    static Behavior<Command> create() {
        return Behaviors.setup(EchoActor::new) as Behavior<Command>
    }

    private EchoActor(ActorContext<Command> context) {
        super(context)
    }

    @Override
    Receive<Command> createReceive() {
        return newReceiveBuilder()
            .onMessage(Msg, this::onMsg)
            .onMessage(FutureResponse, this::onFutureResponse)
            .build()
    }

    private Behavior<Command> onMsg(Msg msg) {
        context.log.info "Msg: ${msg.text}"
        def future = CompletableFuture.supplyAsync(() -> {
            Thread.sleep(500)
            return msg.replyTo
        })
        context.pipeToSelf(
            future,
            (result, exception) -> {
                return new FutureResponse(replyTo: result)
            }
        )
        return this
    }

    private Behavior<Command> onFutureResponse(FutureResponse msg) {
        context.log.info "Received FutureResponse"
        msg.replyTo.tell(Done.INSTANCE)
        return this
    }
}
