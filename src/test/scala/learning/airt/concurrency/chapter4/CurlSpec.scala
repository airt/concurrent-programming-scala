package learning.airt.concurrency.chapter4

import org.scalatest._

import scala.concurrent.TimeoutException

class CurlSpec extends AsyncFreeSpec with Matchers {

  "Exercises in Chapter 4" - {

    "Curl" - {
      "should work correctly" in {
        noException should be thrownBy {
          Curl.main(Array[String]("https://example.org/"))
        }
      }
      "should get response correctly" in {
        Curl.apply("https://example.org/") map { res =>
          res should include("Example Domain")
        } recover {
          case _: TimeoutException => succeed
        }
      }
      "should fail with correct exception" in {
        Curl.apply("https://bad-url.zzzzzz/").failed map { e =>
          e.getClass shouldBe classOf[java.net.UnknownHostException]
        }
      }
    }

  }

}
