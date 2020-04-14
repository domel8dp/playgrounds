import akka.actor.typed.ActorSystem
import pgr.supervision.Guardian

def system = ActorSystem.create(Guardian.create(), "guardian")

// also in Guardian: timers

// restarting strategy
// Exception in child -> PreRestart signal in Child, new Child instance, no Terminated(ChildFailed) in Parent
//system.tell(Guardian.Action.CRASH_CHILD)

// restarting strategy
// Exception in parent -> PreRestart signal in Parent, PostStop signal in Child, new Parent instance, new Child instance
//system.tell(Guardian.Action.CRASH)

// escalating strategy
// Exception in child -> PostStop signal in Child, PostStop signal in Parent
// parent must watch child, but not handle Terminated
// the original exception is lost, Guardian sees only DeathPactException
system.tell(Guardian.Action.CRASH_ESCALATING_CHILD)