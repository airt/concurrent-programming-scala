package learning.airt.concurrency.chapter3

import learning.airt.concurrency.chapter2.thread
import org.scalatest._

import scala.collection.JavaConverters._

class TreiberStackSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 3" - {

    "TreiberStack" - {
      "should work correctly" in {
        val stack = new TreiberStack[Int]
        val rs = new java.util.concurrent.LinkedBlockingQueue[Int]
        (1 to 100) map { i =>
          thread {
            stack push i
          }
        } foreach (_ join ())
        (1 to 100) map { _ =>
          thread {
            rs put (stack pop ())
          }
        } foreach (_ join ())
        rs.asScala.toSeq.sorted shouldEqual (1 to 100)
      }
    }

  }

}
