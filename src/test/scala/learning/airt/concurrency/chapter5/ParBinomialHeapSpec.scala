package learning.airt.concurrency.chapter5

import org.scalatest._

import scala.util.Random

class ParBinomialHeapSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 5" - {

    "ParBinomialHeap" - {

      "splitter" - {
        "should work correctly" in {
          val ph = (BinomialHeap from (Random shuffle (1 to 15))).par
          val r = (ph fold 0) (_ + _)
          r shouldBe (1 to 15).sum
        }
      }

    }

  }

}
