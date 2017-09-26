package learning.concurrent.programming.chapter4

import org.scalatest._

import scala.concurrent._

class FutureOpsExistsSpec extends AsyncFreeSpec with Matchers {

  "Exercises in Chapter 4" - {

    "FutureOpsExistsF" - {
      "should return true when `p` returns true" in {
        import FutureOpsExists.FutureOpsExistsContainerF._
        Future(1) exists (_ > 0) map (_ shouldBe true)
      }
      "should return false when `p` returns false" in {
        import FutureOpsExists.FutureOpsExistsContainerF._
        Future(1) exists (_ < 0) map (_ shouldBe false)
      }
      "should return false when future computation failed" in {
        import FutureOpsExists.FutureOpsExistsContainerF._
        Future[Int](throw new RuntimeException) exists (_ > 0) map (_ shouldBe false)
      }
    }

    "FutureOpsExistsP" - {
      "should return true when `p` returns true" in {
        import FutureOpsExists.FutureOpsExistsContainerP._
        Future(1) exists (_ > 0) map (_ shouldBe true)
      }
      "should return false when `p` returns false" in {
        import FutureOpsExists.FutureOpsExistsContainerP._
        Future(1) exists (_ < 0) map (_ shouldBe false)
      }
      "should return false when future computation failed" in {
        import FutureOpsExists.FutureOpsExistsContainerP._
        Future[Int](throw new RuntimeException) exists (_ > 0) map (_ shouldBe false)
      }
    }

    "FutureOpsExistsA" - {
      "should return true when `p` returns true" in {
        import FutureOpsExists.FutureOpsExistsContainerA._
        Future(1) exists (_ > 0) map (_ shouldBe true)
      }
      "should return false when `p` returns false" in {
        import FutureOpsExists.FutureOpsExistsContainerA._
        Future(1) exists (_ < 0) map (_ shouldBe false)
      }
      "should return false when future computation failed" in {
        import FutureOpsExists.FutureOpsExistsContainerA._
        Future[Int](throw new RuntimeException) exists (_ > 0) map (_ shouldBe false)
      }
    }

  }

}
