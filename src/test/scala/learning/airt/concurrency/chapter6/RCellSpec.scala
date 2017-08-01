package learning.airt.concurrency.chapter6

import org.scalatest._

class RCellSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 6" - {

    "RCell" - {

      ":=" - {
        "should work correctly" in {
          val cell = RCell[Int]()
          a[NoSuchElementException] should be thrownBy cell()
          cell := 1
          cell() shouldBe 1
          cell := 2
          cell() shouldBe 2
        }
      }

    }

  }

}
