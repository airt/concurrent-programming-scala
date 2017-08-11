package learning.airt.concurrency.chapter7

import scala.concurrent.stm._

class TPair[A, B](xi: A, yi: B) {

  private val xr = Ref[A](xi)
  private val yr = Ref[B](yi)

  def first(implicit txn: InTxn): A = xr.single()

  def first_=(x: A)(implicit txn: InTxn): Unit = xr.single transform (_ => x)

  def second(implicit txn: InTxn): B = yr.single()

  def second_=(y: B)(implicit txn: InTxn): Unit = yr.single transform (_ => y)

  def swap()(implicit evidence: A =:= B, txn: InTxn): Unit = {
    val t = first
    first = second.asInstanceOf[A]
    second = evidence(t)
  }

}

object TPair {

  def apply[A, B](x: A, y: B): TPair[A, B] = new TPair(x, y)

}
