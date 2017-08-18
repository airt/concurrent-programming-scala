package learning.airt.concurrency.chapter5

import org.scalatest._

class TimedAllocatingSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 5" - {

    "TimedAllocating" - {
      "should work correctly" in {
        noException should be thrownBy (TimedAllocating main Array())
      }
    }

  }

}
