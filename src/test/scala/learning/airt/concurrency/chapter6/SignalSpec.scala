package learning.airt.concurrency.chapter6

import org.scalatest._
import rx.lang.scala.Subject

class SignalSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 6" - {

    "Signal" - {

      "apply" - {
        "should work correctly" in {
          val subject = Subject[Int]
          val signal = Signal from subject
          a[NoSuchElementException] should be thrownBy signal()
          subject onNext 1
          signal() shouldBe 1
          subject onNext 2
          signal() shouldBe 2
        }
      }

      "map" - {
        "should work correctly" in {
          val subject = Subject[Int]
          val signalA = Signal from subject
          subject onNext 1
          val signal = signalA map (_ + 1)
          signal() shouldBe 2
          subject onNext 2
          signal() shouldBe 3
        }
      }

      "zip" - {
        "should work correctly" in {
          val subject = Subject[Int]
          val signalA = Signal from subject
          subject onNext 1
          val signal = signalA zip (signalA map (_ + 1))
          signal() shouldBe ((1, 2))
          subject onNext 2
          signal() shouldBe ((2, 3))
        }
      }

      "scan" - {
        "should work correctly" in {
          val subject = Subject[Int]
          val signalA = Signal from subject
          subject onNext 1
          val signal = (signalA scan 0)(_ + _)
          signal() shouldBe 0
          subject onNext 2
          signal() shouldBe 2
          subject onNext 3
          signal() shouldBe 5
        }
      }

    }

  }

}
