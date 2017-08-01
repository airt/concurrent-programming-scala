package learning.airt.concurrency.chapter6

import rx.lang.scala.Subject

class RCell[A] protected(override val ob: Subject[A]) extends Signal[A](ob) {

  def :=(v: A) {
    ob onNext v
  }

}

object RCell {

  def apply[A](): RCell[A] = new RCell(Subject[A])

}
