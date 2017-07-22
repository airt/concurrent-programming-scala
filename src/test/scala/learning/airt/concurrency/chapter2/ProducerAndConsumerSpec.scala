package learning.airt.concurrency.chapter2

import org.scalatest._

class ProducerAndConsumerSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 2" - {

    "ProducerAndConsumer" - {

      "run" - {
        "should run correctly" in {
          noException should be thrownBy ProducerAndConsumer.run()
        }
      }

      "runWithWait" - {
        "should run correctly" in {
          noException should be thrownBy ProducerAndConsumer.runWithWait()
        }
      }

      "runWithQueue" - {
        "should run correctly" in {
          noException should be thrownBy ProducerAndConsumer.runWithQueue()
        }
      }

    }

  }

}
