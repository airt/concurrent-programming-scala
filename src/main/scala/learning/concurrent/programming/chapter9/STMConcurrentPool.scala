package learning.concurrent.programming.chapter9

import scala.collection.immutable.Queue
import scala.concurrent.stm._

class STMConcurrentPool[A] extends ConcurrentPool[A] {

  private val queue = Ref(Queue[A]())

  override def add(v: A): Unit = atomic { implicit txn =>
    queue() = queue() enqueue v
  }

  override def remove(): A = atomic { implicit txn =>
    val (v, q) = queue().dequeue
    queue() = q
    v
  }

  override def isEmpty: Boolean = queue.single().isEmpty

}

object STMConcurrentPool {

  def apply[A](): STMConcurrentPool[A] = new STMConcurrentPool

}
