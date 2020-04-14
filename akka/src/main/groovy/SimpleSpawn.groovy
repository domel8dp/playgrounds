import akka.actor.typed.ActorSystem
import pgr.simple.Guardian

def system = ActorSystem.create(Guardian.create(), "guardian")

// also in EchoActor: pipe future result to self

system.tell(Guardian.Start.INSTANCE)
