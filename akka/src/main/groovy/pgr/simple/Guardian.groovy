package pgr.simple

import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive

class Guardian extends AbstractBehavior<Command> {

    interface Command { }
    enum Start implements Command { INSTANCE }
    private enum Shutdown implements Command { INSTANCE }

    static Behavior<Command> create() {
        return Behaviors.setup(Guardian::new) as Behavior<Command>
    }

    final def echoActor
    final def echoResponseAdapter

    private Guardian(ActorContext<Command> context) {
        super(context)
        echoActor = context.spawn(EchoActor.create(), "echo")
        echoResponseAdapter = context.messageAdapter(EchoActor.Response, notUsed -> Shutdown.INSTANCE)
    }

    @Override
    Receive<Command> createReceive() {
        return newReceiveBuilder()
            .onMessage(Start, notUsed -> onStart())
            .onMessage(Shutdown, notUsed -> onShutdown())
            .build()
    }

    private Behavior<Command> onStart() {
        context.log.info "Starting"
        echoActor.tell(new EchoActor.Msg(text: 'hello', replyTo: echoResponseAdapter))
        return this
    }

    private Behavior<Command> onShutdown() {
        context.log.info "Stopping system"
        context.system.terminate()
        return this
    }
}
