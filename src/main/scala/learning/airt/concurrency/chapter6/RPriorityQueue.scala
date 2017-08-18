package learning.airt.concurrency.chapter6

import rx.lang.scala._

import scala.collection.mutable

class RPriorityQueue[A: Ordering] {

  private val queue = mutable.PriorityQueue[A]()(implicitly[Ordering[A]].reverse)
  private val subject = Subject[A]

  def add(v: A): Unit = queue += v

  def pop(): A = {
    val v = queue dequeue ()
    subject onNext v
    v
  }

  def popped: Observable[A] = subject

}

object RPriorityQueue {

  def apply[A: Ordering](): RPriorityQueue[A] = new RPriorityQueue

}
