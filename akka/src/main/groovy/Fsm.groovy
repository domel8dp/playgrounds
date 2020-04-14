import akka.actor.typed.ActorSystem
import pgr.fsm.Guardian

def system = ActorSystem.create(Guardian.create(), "guardian")

system.tell(Guardian.MoveToNext.INSTANCE)
