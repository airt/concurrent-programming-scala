package learning.airt.concurrency.chapter8

import akka.actor._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration._

class TimerActor extends Actor {

  import TimerActor._

  def receive: Receive = {
    case Register(t) =>
      context.system.scheduler scheduleOnce (t, sender(), Timeout)
  }

}

object TimerActor {

  def props: Props = Props[TimerActor]

  case class Register(t: FiniteDuration)

  case object Timeout

}
