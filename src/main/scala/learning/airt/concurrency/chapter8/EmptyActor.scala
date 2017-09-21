package learning.airt.concurrency.chapter8

import akka.actor._

class EmptyActor extends Actor {

  def receive: Receive = PartialFunction.empty

}
