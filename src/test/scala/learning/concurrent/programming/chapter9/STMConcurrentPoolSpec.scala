package learning.concurrent.programming.chapter9

import org.scalatest._

class STMConcurrentPoolSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 9" - {

    "STMConcurrentPool" - {

      "should work correctly" in {
        val n = 100
        val pool = STMConcurrentPool[Int]()
        pool.isEmpty shouldBe true
        (1 to n).par foreach (_ => pool add 0)
        pool.isEmpty shouldBe false
        (1 to (n * 2)).par foreach { i =>
          if (i % 2 == 0)
            pool add i
          else
            pool remove ()
        }
        pool.isEmpty shouldBe false
        (1 to n).par foreach (_ => pool remove ())
        pool.isEmpty shouldBe true
      }

    }

  }

}
