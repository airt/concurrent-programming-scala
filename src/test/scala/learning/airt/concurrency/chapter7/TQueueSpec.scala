package learning.airt.concurrency.chapter7

import java.util.concurrent.atomic.AtomicInteger

import org.scalatest._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent._
import scala.concurrent.stm._

class TQueueSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 7" - {

    "TQueue" - {

      "enqueue" - {
        "should work correctly" in {
          val queue = TQueue[Int]()
          atomic { implicit txn =>
            queue enqueue 1
            queue dequeue () shouldBe 1
          }
        }
      }

      "dequeue" - {
        "should work correctly" in {
          val queue = TQueue[Int]()
          val step = new AtomicInteger(0)
          val ra1 = Future {
            val v = atomic { implicit txn =>
              queue dequeue ()
            }
            v shouldBe 1
            step incrementAndGet () shouldBe 2
          }
          val ra2 = Future {
            step incrementAndGet () shouldBe 1
            atomic { implicit txn =>
              queue enqueue 1
            }
          }
          Await ready (ra1 zip ra2, duration.Duration.Inf)
        }
      }

    }

  }

}
