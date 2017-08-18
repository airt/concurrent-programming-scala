package learning.airt.concurrency.chapter7

import java.util.concurrent.atomic.AtomicInteger

import org.scalatest._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent._
import scala.concurrent.stm._

class MVarSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 7" - {

    "MVar" - {

      "put" - {
        "should work correctly" in {
          val mv = MVar[Int]()
          val step = new AtomicInteger(0)
          val ra1 = Future {
            atomic { implicit txn =>
              mv put 1
            }
            atomic { implicit txn =>
              mv put 2
            }
            step incrementAndGet () shouldBe 2
          }
          val ra2 = Future {
            step incrementAndGet () shouldBe 1
            val v = atomic { implicit txn =>
              mv take ()
            }
            v shouldBe 1
          }
          Await ready (ra1 zip ra2, duration.Duration.Inf)
        }
      }

      "take" - {
        "should work correctly" in {
          val mv = MVar[Int]()
          val step = new AtomicInteger(0)
          val ra1 = Future {
            val v = atomic { implicit txn =>
              mv take ()
            }
            v shouldBe 1
            step incrementAndGet () shouldBe 2
          }
          val ra2 = Future {
            step incrementAndGet () shouldBe 1
            atomic { implicit txn =>
              mv put 1
            }
          }
          Await ready (ra1 zip ra2, duration.Duration.Inf)
        }
      }

      "swap" - {
        "should work correctly" in {
          val xmv = MVar[Int]()
          val ymv = MVar[Int]()
          atomic { implicit txn =>
            xmv put 1
            ymv put 2
          }
          val ra = Future sequence ((1 to 99) map { _ =>
            Future {
              atomic { implicit txn =>
                MVar swap (xmv, ymv)
              }
            }
          })
          Await ready (ra, duration.Duration.Inf)
          atomic { implicit txn =>
            xmv take ()
          } shouldBe 2
          atomic { implicit txn =>
            ymv take ()
          } shouldBe 1
        }
      }

    }

  }

}
