package learning.concurrent.programming.chapter5

import org.scalatest._

import scala.util.Random

class ParBinomialHeapSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 5" - {

    "ParBinomialHeap" - {

      "splitter" - {
        "should work correctly" in {
          val heap = BinomialHeap from (Random shuffle (1 to 15))
          (heap.par fold 0)(_ + _) shouldBe (1 to 15).sum
        }
      }

      "combiner" - {
        "should work correctly" in {
          val heap = BinomialHeap from (Random shuffle (1 to 15))
          (heap.par filter (_ % 2 == 1)).seq.toSeq shouldBe (1 to 15 by 2)
        }
      }

    }

  }

}
