package learning.concurrent.programming.chapter4

import org.scalatest._

class IVarSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 4" - {

    "IVar" - {
      "should throw when get from empty" in {
        val v = IVar[Int]()
        a[NoSuchElementException] should be thrownBy {
          v()
        }
      }
      "should throw when set to nonempty" in {
        val v = IVar[Int]()
        v := 1
        a[IllegalStateException] should be thrownBy {
          v := 2
        }
      }
      "could set value to empty" in {
        val v = IVar[Int]()
        noException should be thrownBy {
          v := 2
        }
      }
      "should return value when get from nonempty" in {
        val v = IVar[Int]()
        v := 1
        v() shouldBe 1
      }
    }

  }

}
