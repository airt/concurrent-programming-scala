package learning.concurrent.programming.chapter4

import org.scalatest._

class IMapSpec extends AsyncFreeSpec with Matchers {

  "Exercises in Chapter 4" - {

    "IMap" - {

      "update" - {
        "should throw when contains key" in {
          val m = IMap[Int, Int]()
          m update (1, 2)
          a[IllegalStateException] should be thrownBy {
            m update (1, 2)
          }
        }
      }

      "apply" - {
        "should not complete initially" in {
          val m = IMap[Int, Int]()
          m(1).isCompleted shouldBe false
        }
        "should complete after put" in {
          val m = IMap[Int, Int]()
          m update (1, 2)
          m(1) map (_ shouldBe 2)
        }
      }

    }

  }

}
