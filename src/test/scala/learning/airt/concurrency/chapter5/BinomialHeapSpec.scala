package learning.airt.concurrency.chapter5

import org.scalatest._

import scala.util.Random

class BinomialHeapSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 5" - {

    "BinomialHeap" - {

      "apply" - {
        "should work correctly" in {
          val heap = BinomialHeap from (Random shuffle (1 to 15))
          heap.toSeq shouldBe (1 to 15)
        }
      }

      "insert" - {
        "should work correctly" in {
          val heap = BinomialHeap(1, 5)
          val heapN = heap insert 3
          heapN.toSeq shouldBe Seq(1, 3, 5)
        }
      }

      "remove" - {
        "should work correctly" in {
          val heap = BinomialHeap from (Random shuffle (1 to 15))
          val (value, heapN) = heap.remove
          value shouldBe 1
          heapN.toSeq shouldBe (2 to 15)
        }
      }

      "minimum" - {
        "should work correctly" in {
          val heap = BinomialHeap from (Random shuffle (1 to 15))
          heap.minimum shouldBe 1
        }
      }

      "merge" - {
        "should work correctly" in {
          // noinspection RedundantCollectionConversion
          val heapB = BinomialHeap from (Random shuffle (2 to 15 by 2).toIndexedSeq)
          // noinspection RedundantCollectionConversion
          val heapA = BinomialHeap from (Random shuffle (1 to 15 by 2).toIndexedSeq)
          heapA.size shouldBe 8
          heapB.size shouldBe 7
          val heapN = heapA merge heapB
          heapN.size shouldBe 15
          heapN.toSeq shouldBe (1 to 15)
        }
      }

      "size" - {
        "should work correctly" in {
          val heap = BinomialHeap from (Random shuffle (1 to 15))
          heap.size shouldBe 15
          heap.iterator.size shouldBe 15
        }
      }

      "show" - {
        "should work correctly" in {
          val heap = BinomialHeap from (1 to 15)
          heap.show shouldBe
            """
              |BinomialHeap {
              |15 (0)
              |,
              |13 (1)
              |  14 (0)
              |,
              |9 (2)
              |  10 (0)
              |  11 (1)
              |    12 (0)
              |,
              |1 (3)
              |  2 (0)
              |  3 (1)
              |    4 (0)
              |  5 (2)
              |    6 (0)
              |    7 (1)
              |      8 (0)
              |}
            """.stripMargin.trim
        }
      }

    }

    "BinomialTree" - {

      "zero" - {
        "should work correctly" in {
          val tree = BinomialTree zero 'x'
          tree.value shouldBe 'x'
          tree.order shouldBe 0
          tree.children shouldBe Nil
        }
      }

      val trees = BinomialTree repeat 'x'

      "/::" - {
        "should work correctly" in {
          val treeA = BinomialTree zero 'a'
          val treeB = BinomialTree zero 'b'
          val treeN = treeA /:: treeB
          treeN.leftmost shouldBe treeA
          treeN.value shouldBe treeB.value
          treeN.order shouldBe (treeB.order + 1)
          treeN.children.tail shouldBe treeB.children
        }
        "should fail when args have different orders" in {
          an[IllegalArgumentException] should be thrownBy {
            trees(1) /:: trees(2)
          }
        }
      }

      "split" - {
        "should work correctly" in {
          val Some((treeA, treeB)) = trees(3).split
          treeA shouldBe trees(2)
          treeB shouldBe trees(2)
        }
      }

      "size" - {
        "should work correctly" in {
          def strictSize[A](tree: BinomialTree[A]): Int =
            1 + (tree.children map strictSize).sum

          trees.zipWithIndex take 10 foreach { case (tree, index) =>
            tree.order shouldBe index
            tree.size shouldBe strictSize(tree)
          }
        }
      }

      "show" - {
        "should work correctly" in {
          trees(3).show shouldBe
            """
              |x (3)
              |  x (0)
              |  x (1)
              |    x (0)
              |  x (2)
              |    x (0)
              |    x (1)
              |      x (0)
            """.
              stripMargin.trim
        }
      }

    }

  }

}
