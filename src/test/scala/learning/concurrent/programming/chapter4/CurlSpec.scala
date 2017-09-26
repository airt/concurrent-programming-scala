package learning.concurrent.programming.chapter4

import org.scalatest._

import scala.concurrent.TimeoutException

class CurlSpec extends AsyncFreeSpec with Matchers {

  "Exercises in Chapter 4" - {

    "Curl" - {
      "should work correctly" in {
        noException should be thrownBy {
          Curl main Array[String]("https://example.org", "--silent")
        }
      }
      "should get response correctly" in {
        Curl apply ("https://example.org", silent = true) map { res =>
          res should include("Example Domain")
        } recover {
          case _: TimeoutException => succeed
        }
      }
      "should fail with correct exception" in {
        (Curl apply ("https://localhost:65535", silent = true)).failed map { e =>
          e should (be(a[java.net.ConnectException]) or be(a[TimeoutException]))
        }
      }
    }

  }

}
