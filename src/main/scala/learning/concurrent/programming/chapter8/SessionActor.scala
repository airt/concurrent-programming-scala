package learning.concurrent.programming.chapter8

import akka.actor._

class SessionActor(password: String, target: ActorRef) extends Actor {

  import SessionActor._

  def receive: Receive = waiting

  private def waiting: Receive = {
    case StartSession(`password`) => context become processing
  }

  private def processing: Receive = {
    case StopSession => context become waiting
    case message => target forward message
  }

}

object SessionActor {

  def props(password: String, target: ActorRef) = Props(new SessionActor(password, target))

  case class StartSession(password: String)

  case object StopSession

}
