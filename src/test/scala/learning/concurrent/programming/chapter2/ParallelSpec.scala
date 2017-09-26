package learning.concurrent.programming.chapter2

import org.scalatest._

class ParallelSpec extends FreeSpec with Matchers {

  import Parallel._

  "Exercises in Chapter 2" - {

    "parallel" - {
      "should work correctly" in {
        parallel(1)(2) shouldBe (1, 2)
      }
    }

  }

}
