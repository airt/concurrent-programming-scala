package learning.airt.concurrency.chapter7

import scala.collection.immutable.Queue
import scala.concurrent.stm._

class TQueue[A] {

  private val qr = Ref(Queue[A]())

  def enqueue(v: A)(implicit txn: InTxn): Unit = qr() = qr() enqueue v

  def dequeue()(implicit txn: InTxn): A = qr().dequeueOption match {
    case Some((v, q)) => qr() = q; v
    case None => retry
  }

}

object TQueue {

  def apply[A](): TQueue[A] = new TQueue

}
