package learning.airt.concurrency.chapter7

import java.util.concurrent.atomic.AtomicInteger

import scala.concurrent.stm._

object RollbackCounter {

  def atomicRollbackCount[A](f: InTxn => A): (A, Int) = {
    val c = new AtomicInteger(0)
    atomic { implicit txn =>
      Txn afterRollback (_ => c incrementAndGet())
      (f(txn), c get())
    }
  }

}
