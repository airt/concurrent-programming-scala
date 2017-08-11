package learning.airt.concurrency.chapter2

import java.util.concurrent.atomic.AtomicInteger

import org.scalatest._

class SyncVarSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 2" - {

    "SyncVar" - {

      "get" - {
        "should return value" in {
          val sv = new SyncVar[Int]
          sv put 1 get() shouldBe 1
        }
        "should throw exception when empty" in {
          val sv = new SyncVar[Int]
          a[NoSuchElementException] should be thrownBy (sv get())
        }
        "should become empty after `get`" in {
          val sv = new SyncVar[Int]
          sv put 1 get()
          a[NoSuchElementException] should be thrownBy (sv get())
        }
      }

      "put" - {
        "should hold value" in {
          val sv = new SyncVar[Int]
          sv put 1 get() shouldBe 1
        }
        "should throw exception when nonempty" in {
          val sv = new SyncVar[Int]
          sv put 1
          a[IllegalStateException] should be thrownBy (sv put 1)
        }
      }

      "getWait" - {
        "should work correctly" in {
          val sv = new SyncVar[Int]
          val step = new AtomicInteger(0)
          val t1 = thread {
            val v = sv getWait()
            v shouldBe 1
            step incrementAndGet() shouldBe 2
          }
          val t2 = thread {
            step incrementAndGet() shouldBe 1
            sv putWait 1
          }
          t1 join()
          t2 join()
        }
      }

      "putWait" - {
        "should work correctly" in {
          val sv = new SyncVar[Int]
          val step = new AtomicInteger(0)
          val t1 = thread {
            sv putWait 1 putWait 2
            step incrementAndGet() shouldBe 2
          }
          val t2 = thread {
            step incrementAndGet() shouldBe 1
            val v = sv getWait()
            v shouldBe 1
          }
          t1 join()
          t2 join()
        }
      }

      "empty" - {
        "should be empty initially" in {
          val sv = new SyncVar[Int]
          sv.isEmpty shouldBe true
          sv.nonEmpty shouldBe false
        }
        "should be nonempty after `put`" in {
          val sv = new SyncVar[Int]
          sv put 1
          sv.isEmpty shouldBe false
          sv.nonEmpty shouldBe true
        }
        "should be empty after `get`" in {
          val sv = new SyncVar[Int]
          sv put 1 get()
          sv.isEmpty shouldBe true
          sv.nonEmpty shouldBe false
        }
      }

    }

  }

}
