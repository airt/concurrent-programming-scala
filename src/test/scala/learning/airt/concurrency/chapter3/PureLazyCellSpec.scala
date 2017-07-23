package learning.airt.concurrency.chapter3

import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger}

import learning.airt.concurrency.chapter2.thread
import org.scalatest._

class PureLazyCellSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 3" - {

    "PureLazyCell" - {
      "should initialize after get" in {
        val initialized = new AtomicBoolean(false)
        val cell = PureLazyCell {
          initialized set true
          1
        }
        initialized.get() shouldBe false
        val v = cell()
        initialized.get() shouldBe true
        v shouldBe 1
      }
      "should initialize only once" in {
        val count = new AtomicInteger(0)
        val cell = PureLazyCell {
          count.incrementAndGet()
          1
        }
        count.get() shouldBe 0
        val v1 = cell()
        val v2 = cell()
        count.get() shouldBe 1
        v1 shouldBe 1
        v2 shouldBe 1
      }
      "should initialize without locking" in {
        val count = new AtomicInteger(0)
        val cell = PureLazyCell {
          count.incrementAndGet()
          1
        }
        (1 to 100) map { _ => thread(cell()) } foreach (_ join())
        count.get() should be <= 100
      }
    }

  }

}
