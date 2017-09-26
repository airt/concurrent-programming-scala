package learning.concurrent.programming.chapter4

import org.scalatest._

import scala.concurrent._

class PromiseOpsComposeSpec extends AsyncFreeSpec with Matchers {

  "Exercises in Chapter 4" - {

    "PromiseOpsCompose" - {
      "should work correctly" in {
        import PromiseOpsCompose._
        val promise1 = Promise[Boolean]
        val promise2 = promise1 compose ((x: Int) => x > 0)
        promise2 success 1
        promise1.future map (_ shouldBe true)
      }
    }

  }

}
