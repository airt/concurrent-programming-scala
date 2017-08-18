package learning.airt.concurrency.chapter2

import org.scalatest._

class PeriodicallySpec extends FreeSpec with Matchers {

  import Periodically._

  "Exercises in Chapter 2" - {

    "periodically" - {
      "should work correctly" in {
        import scala.concurrent.duration._
        var i = 0
        periodically(1.millisecond) {
          i += 1
        }
        Thread sleep 10
        i should be > 0
      }
    }

  }

}
