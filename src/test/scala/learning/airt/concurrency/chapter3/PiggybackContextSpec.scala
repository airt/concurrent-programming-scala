package learning.airt.concurrency.chapter3

import org.scalatest._

class PiggybackContextSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 3" - {

    "PiggybackContext" - {
      "should run task in current thread" in {
        val context = new PiggybackContext
        val currentThreadName = Thread.currentThread().getName
        context execute { () =>
          Thread.currentThread().getName shouldBe currentThreadName
        }
      }
      "could run `execute` inside task" in {
        val context = new PiggybackContext
        val currentThreadName = Thread.currentThread().getName
        context execute { () =>
          context execute { () =>
            Thread.currentThread().getName shouldBe currentThreadName
          }
        }
      }
      "should handle exception" in {
        val context = new PiggybackContext
        noException should be thrownBy {
          context execute { () =>
            throw new Exception("piggyback task exception")
          }
        }
      }
    }

  }

}
