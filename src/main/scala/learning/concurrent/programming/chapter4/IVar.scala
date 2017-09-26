package learning.concurrent.programming.chapter4

import scala.concurrent._

class IVar[A] {

  private val promise = Promise[A]

  def apply(): A =
    if (promise.isCompleted) Await result (promise.future, duration.Duration.Inf)
    else throw new NoSuchElementException

  def :=(x: A) {
    if (!(promise trySuccess x)) throw new IllegalStateException
  }

}

object IVar {

  def apply[A](): IVar[A] = new IVar[A]

}
