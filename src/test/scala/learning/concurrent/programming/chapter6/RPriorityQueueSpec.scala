package learning.concurrent.programming.chapter6

import java.util.concurrent.atomic.AtomicInteger

import org.scalatest._

class RPriorityQueueSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 6" - {

    "RPriorityQueue" - {

      "apply and update" - {
        "should work correctly" in {
          val queue = RPriorityQueue[Int]()
          val ob = queue.popped
          val counter = new AtomicInteger(0)
          ob subscribe { x =>
            x shouldBe (counter incrementAndGet ())
          }
          queue add 2
          queue add 1
          queue pop ()
          queue pop ()
          Thread sleep 10
          counter get () shouldBe 2
        }
      }

    }

  }

}
