package learning.airt.concurrency

import akka.actor._

import scala.concurrent._
import scala.concurrent.duration._

package object chapter8 {

  def delay(t: FiniteDuration)(implicit system: ActorSystem, executor: ExecutionContext): Future[Unit] = {
    val promise = Promise[Unit]
    // noinspection ScalaUnnecessaryParentheses
    (system.scheduler scheduleOnce t) { promise success (()) }
    promise.future
  }

}
