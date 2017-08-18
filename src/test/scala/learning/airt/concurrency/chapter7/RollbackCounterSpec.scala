package learning.airt.concurrency.chapter7

import com.typesafe.scalalogging.LazyLogging
import org.scalatest._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent._
import scala.concurrent.stm._

class RollbackCounterSpec extends FreeSpec with Matchers with LazyLogging {

  "Exercises in Chapter 7" - {

    "RollbackCounter" - {

      "atomicRollbackCount" - {
        "should work correctly" in {
          import RollbackCounter.atomicRollbackCount
          val xr = Ref(1)
          val f: InTxn => Int = implicit txn => {
            val x = xr()
            Thread sleep 100
            xr() = x + 1
            x
          }
          val rsa = Future sequence ((1 to 10) map { _ =>
            Future {
              blocking {
                atomicRollbackCount(f)._2
              }
            }
          })
          val rs = Await result (rsa, duration.Duration.Inf)
          logger debug s"rollback counts ${rs filter (_ > 0)}"
        }
      }

    }

  }

}
