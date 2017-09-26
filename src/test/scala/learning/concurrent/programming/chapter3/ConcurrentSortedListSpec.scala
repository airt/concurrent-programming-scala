package learning.concurrent.programming.chapter3

import learning.concurrent.programming.chapter2.thread
import org.scalatest._

import scala.util.Random

class ConcurrentSortedListSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 3" - {

    "ConcurrentSortedList" - {
      "should work correctly" in {
        val list = new ConcurrentSortedList[Int]
        Random shuffle (1 to 100) map { i =>
          thread {
            list add i
          }
        } foreach (_ join ())
        val rs = list.iterator.toSeq
        rs shouldEqual (1 to 100)
        a[NoSuchElementException] should be thrownBy {
          new ConcurrentSortedList[Int].iterator next ()
        }
      }
    }

  }

}
