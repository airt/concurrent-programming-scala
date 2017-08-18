package learning.airt.concurrency.chapter7

import org.scalatest._

import scala.concurrent.stm._

class TPairSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 7" - {

    "TPair" - {

      "first" - {
        "should work correctly" in {
          val pair = TPair(1, 2)
          atomic { implicit txn =>
            pair.first shouldBe 1
          }
        }
      }

      "first_=" - {
        "should work correctly" in {
          val pair = TPair(1, 2)
          atomic { implicit txn =>
            pair.first = 5
            pair.first shouldBe 5
          }
        }
      }

      "second" - {
        "should work correctly" in {
          val pair = TPair(1, 2)
          atomic { implicit txn =>
            pair.second shouldBe 2
          }
        }
      }

      "second_=" - {
        "should work correctly" in {
          val pair = TPair(1, 2)
          atomic { implicit txn =>
            pair.second = 5
            pair.second shouldBe 5
          }
        }
      }

      "swap" - {
        "should work correctly" in {
          val pair = TPair(1, 2)
          atomic { implicit txn =>
            pair swap ()
            pair.first shouldBe 2
            pair.second shouldBe 1
          }
        }
      }

    }

  }

}
