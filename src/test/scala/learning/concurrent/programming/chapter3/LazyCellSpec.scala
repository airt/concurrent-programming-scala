package learning.concurrent.programming.chapter3

import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger}

import org.scalatest._

class LazyCellSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 3" - {

    "LazyCell" - {
      "should initialize after get" in {
        val initialized = new AtomicBoolean(false)
        val cell = LazyCell {
          initialized set true
          1
        }
        initialized get () shouldBe false
        val v = cell()
        initialized get () shouldBe true
        v shouldBe 1
      }
      "should initialize only once" in {
        val count = new AtomicInteger(0)
        val cell = LazyCell {
          count incrementAndGet ()
          1
        }
        count get () shouldBe 0
        val v1 = cell()
        val v2 = cell()
        count get () shouldBe 1
        v1 shouldBe 1
        v2 shouldBe 1
      }
    }

  }

}
