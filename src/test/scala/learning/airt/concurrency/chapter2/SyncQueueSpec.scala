package learning.airt.concurrency.chapter2

import java.util.concurrent.atomic.AtomicInteger

import org.scalatest._

class SyncQueueSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 2" - {

    "SyncQueue" - {

      "getWait" - {
        "should work correctly" in {
          val sq = new SyncQueue[Int](2)
          val step = new AtomicInteger(0)
          val t = thread {
            val v = sq.getWait()
            v shouldBe 1
            step.incrementAndGet() shouldBe 2
          }
          step.incrementAndGet() shouldBe 1
          sq.putWait(1)
          t.join()
        }
      }

      "putWait" - {
        "should work correctly" in {
          val sq = new SyncQueue[Int](2)
          val step = new AtomicInteger(0)
          val t = thread {
            sq.putWait(1).putWait(2).putWait(3)
            step.incrementAndGet() shouldBe 2
          }
          step.incrementAndGet() shouldBe 1
          val v = sq.getWait()
          v shouldBe 1
          t.join()
        }
      }

    }

  }

}
