package learning.airt.concurrency.chapter7

import scala.concurrent.stm._

class MVar[A] {

  private val xr = Ref[Option[A]](None)

  def put(v: A)(implicit txn: InTxn): Unit = xr() match {
    case Some(_) => retry
    case None => xr update Some(v)
  }

  def take()(implicit txn: InTxn): A = xr() match {
    case Some(v) => xr update None; v
    case None => retry
  }

}

object MVar {

  def apply[A](): MVar[A] = new MVar

  def swap[A](x: MVar[A], y: MVar[A])(implicit txn: InTxn) {
    val xv = x take()
    val yv = y take()
    x put yv
    y put xv
  }

}
