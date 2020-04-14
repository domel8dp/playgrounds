import akka.actor.typed.ActorSystem
import pgr.lifecycle.Guardian

def system = ActorSystem.create(Guardian.create(), "guardian")

// context.stop(child) -> PostStop signal in Child, Terminated in Parent
system.tell(Guardian.Action.STOP_CHILD)

// Behaviours.stoppped() in child -> PostStop signal in Child, Terminated in Parent
//system.tell(Guardian.Action.STOP_CHILD_BY_MSG)

// Behaviours.stoppped(customCleanUp) in child -> PostStop signal in Child, customCleanUp in Child, Terminated in Parent
//system.tell(Guardian.Action.STOP_CHILD_BY_MSG_WITH_CUSTOM)

// Exception in child -> PostStop signal in Child, Terminated(ChildFailed) in Parent
// Exception is logged in parent (??)
//system.tell(Guardian.Action.CRASH_CHILD)

// Behaviours.stoppped() in parent -> PostStop signal in Child, PostStop signal in Parent
//system.tell(Guardian.Action.STOP)

// Exception in parent -> PostStop signal in Child, PostStop signal in Parent
//system.tell(Guardian.Action.CRASH)
