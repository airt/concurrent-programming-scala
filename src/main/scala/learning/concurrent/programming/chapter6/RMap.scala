package learning.concurrent.programming.chapter6

import rx.lang.scala._

import scala.collection.mutable

class RMap[A, B] {

  private val internal = mutable.Map[A, Subject[B]]()

  def update(k: A, v: B): Unit = internal get k match {
    case Some(ob) => ob onNext v
    case None =>
  }

  def apply(k: A): Observable[B] = internal getOrElseUpdate (k, Subject())

}

object RMap {

  def apply[A, B](): RMap[A, B] = new RMap

}
