package learning.airt.concurrency.chapter2

import org.scalatest._

class SyncQueueSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 2" - {

    "SyncQueue" - {

      "getWait" - {
        "should work correctly" in {
          val sq = new SyncQueue[Int](2)
          val startTime = System.currentTimeMillis()
          @volatile var delta = 0
          thread {
            val v = sq.getWait()
            v shouldBe 1
            delta = (System.currentTimeMillis() - startTime).toInt
          }
          Thread.sleep(100)
          sq.putWait(1)
          Thread.sleep(10)
          delta should be >= 100
        }
      }

      "putWait" - {
        "should work correctly" in {
          val sq = new SyncQueue[Int](2)
          val startTime = System.currentTimeMillis()
          @volatile var delta = 0
          thread {
            sq.putWait(1).putWait(2).putWait(3)
            delta = (System.currentTimeMillis() - startTime).toInt
          }
          Thread.sleep(100)
          val v = sq.getWait()
          v shouldBe 1
          Thread.sleep(10)
          delta should be >= 100
        }
      }

    }

  }

}
