package learning.concurrent.programming.chapter4

import scala.async.Async._
import scala.concurrent._

object FutureOpsExists {

  object FutureOpsExistsContainerF {

    implicit final class FutureOpsExistsF[A](private val self: Future[A]) extends AnyVal {
      def exists(p: A => Boolean)(implicit executor: ExecutionContext): Future[Boolean] =
        self map p recover { case _ => false }
    }

  }

  object FutureOpsExistsContainerP {

    implicit final class FutureOpsExistsP[A](private val self: Future[A]) extends AnyVal {
      def exists(p: A => Boolean)(implicit executor: ExecutionContext): Future[Boolean] = {
        val promise = Promise[Boolean]
        self map p recover { case _ => false } foreach promise.success
        promise.future
      }
    }

  }

  object FutureOpsExistsContainerA {

    implicit final class FutureOpsExistsA[A](private val self: Future[A]) {
      def exists(p: A => Boolean)(implicit executor: ExecutionContext): Future[Boolean] =
        async(p(await(self))) recover { case _ => false }
    }

  }

}
