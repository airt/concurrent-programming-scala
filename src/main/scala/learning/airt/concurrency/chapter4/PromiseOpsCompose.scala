package learning.airt.concurrency.chapter4

import scala.concurrent._

object PromiseOpsCompose {

  implicit final class PromiseOpsCompose[A](private val self: Promise[A]) extends AnyVal {
    def compose[B](f: B => A)(implicit executor: ExecutionContext): Promise[B] = {
      val promise = Promise[B]
      promise.future onComplete (t => self complete (t map f))
      promise
    }
  }

}
