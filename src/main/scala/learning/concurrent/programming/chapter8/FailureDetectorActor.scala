package learning.concurrent.programming.chapter8

import akka.actor._
import akka.pattern._

import scala.concurrent.duration._

class FailureDetectorActor extends Actor {

  import FailureDetectorActor._

  def receive: Receive = {
    case detect: Detect => watch(detect)(context.system)
  }

  private def watch(detect: Detect)(implicit system: ActorSystem) = {
    import system.dispatcher
    val Detect(target, interval, timeout) = detect
    (system.scheduler schedule (0.seconds, interval)) {
      (target ? Identify(0))(timeout).failed foreach { _ =>
        (system actorSelection target.path.parent) ! Failed(target)
      }
    }
  }

}

object FailureDetectorActor {

  def props: Props = Props[FailureDetectorActor]

  case class Detect(target: ActorRef, interval: FiniteDuration, timeout: FiniteDuration)

  case class Failed(ref: ActorRef)

}
