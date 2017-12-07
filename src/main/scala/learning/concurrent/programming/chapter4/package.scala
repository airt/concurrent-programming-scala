package learning.concurrent.programming

import scala.concurrent._
import scala.concurrent.duration.Duration
import scala.util._

package object chapter4 {

  def delay(t: Duration): Future[Unit] = {
    import java.util.{Timer, TimerTask}
    val promise = Promise[Unit]
    val timer = new Timer(true)
    val task = new TimerTask {
      // noinspection ScalaUnnecessaryParentheses
      override def run(): Unit = promise success (())
    }
    timer schedule (task, t.toMillis)
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

    def withTimeout(t: Duration)(implicit executor: ExecutionContext): Future[A] =
      delay(t) race self map {
        case Left(_)  => throw new TimeoutException
        case Right(x) => x
      }

  }

}
