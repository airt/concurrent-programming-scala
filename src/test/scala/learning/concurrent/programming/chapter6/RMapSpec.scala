package learning.concurrent.programming.chapter6

import java.util.concurrent.atomic.AtomicInteger

import org.scalatest._

class RMapSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 6" - {

    "RMap" - {

      "apply and update" - {
        "should work correctly" in {
          val map = RMap[Int, Int]()
          val ob = map(9)
          val counter = new AtomicInteger(0)
          ob subscribe { x =>
            x shouldBe (counter incrementAndGet ())
          }
          map(9) = 1
          map(9) = 2
          Thread sleep 10
          counter get () shouldBe 2
        }
      }

    }

  }

}
