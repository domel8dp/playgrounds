package pgr.supervision

import akka.actor.typed.Behavior
import akka.actor.typed.SupervisorStrategy
import akka.actor.typed.Terminated
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive
import akka.actor.typed.javadsl.TimerScheduler

import java.time.Duration

class Guardian extends AbstractBehavior<Command> {

    interface Command { }
    enum Action implements Command { CRASH_CHILD, CRASH, CRASH_ESCALATING_CHILD }
    private enum Shutdown implements Command { INSTANCE }

    static Behavior<Command> create() {
        return Behaviors.<Command>withTimers(timers -> {
            Behaviors.<Command>setup(ctx -> new Guardian(ctx, timers))
        })
    }

    final TimerScheduler<Command> timers
    final def parentActor
    final def escalatingParentActor

    private Guardian(ActorContext<Command> context, TimerScheduler<Command> timers) {
        super(context)
        this.timers = timers
        def parentBehavior = Behaviors.supervise(Parent.create())
                .onFailure(SupervisorStrategy.restart())
        parentActor = context.spawn(parentBehavior, "parent")
        context.watch(parentActor)
        escalatingParentActor = context.spawn(EscalatingParent.create(), "escalating_parent")
        context.watch(escalatingParentActor)
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
        def (target, action) = map(msg)
        if (action) {
            target.tell(action)
            timers.startSingleTimer(Shutdown.INSTANCE, Duration.ofMillis(200))
            return this
        } else {
            context.log.info "Ignoring unknown scenario: ${msg}"
            return this
        }
    }

    private def map(Action msg) {
        switch (msg) {
            case Action.CRASH_CHILD: return [parentActor, Parent.Action.CRASH_CHILD]
            case Action.CRASH_ESCALATING_CHILD: return [escalatingParentActor, EscalatingParent.CrashChild.INSTANCE]
            case Action.CRASH: return [parentActor, Parent.Action.CRASH]
            default: return [null, null]
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
