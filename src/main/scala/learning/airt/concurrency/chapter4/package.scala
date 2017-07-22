package learning.airt.concurrency

import java.util.{Timer, TimerTask}

import scala.concurrent._
import scala.util._

package object chapter4 {

  def delay(t: Long): Future[Unit] = {
    val promise = Promise[Unit]
    val timer = new Timer
    timer.schedule(new TimerTask {
      override def run() {
        promise.success(())
        timer.cancel()
      }
    }, t)
    promise.future
  }

  implicit final class FutureOps[A](private val self: Future[A]) extends AnyVal {

    def race[B](rhs: Future[B])(implicit executor: ExecutionContext): Future[Either[A, B]] = {
      val promise = Promise[Either[A, B]]
      self foreach (promise trySuccess Left(_))
      rhs foreach (promise trySuccess Right(_))
      Seq(self, rhs) foreach (_.failed foreach promise.tryFailure)
      promise.future
    }

    def withTimeout(t: Long)(implicit executor: ExecutionContext): Future[A] = {
      delay(t) race self map {
        case Left(_) => throw new TimeoutException
        case Right(x) => x
      }
    }

  }

}
