package learning.concurrent.programming.chapter3

import java.util.concurrent.atomic.AtomicReference

import scala.annotation.tailrec

class PureLazyCell[A](initialization: => A) {

  private[this] val vor = new AtomicReference[Option[A]](None)

  def apply(): A = apply(initialization)

  @tailrec
  private def apply(vc: => A): A = vor get () match {
    case Some(v) => v
    case None =>
      val v = vc
      if (!(vor compareAndSet (None, Some(v)))) apply(v)
      else v
  }

}

object PureLazyCell {

  def apply[A](initialization: => A): PureLazyCell[A] = new PureLazyCell(initialization)

}
