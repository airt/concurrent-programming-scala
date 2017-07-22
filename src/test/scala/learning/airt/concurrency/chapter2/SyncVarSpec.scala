package learning.airt.concurrency.chapter2

import org.scalatest._

class SyncVarSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 2" - {

    "SyncVar" - {

      "get" - {
        "should return value" in {
          val sv = new SyncVar[Int]
          sv.put(1).get() shouldBe 1
        }
        "should throw exception when empty" in {
          val sv = new SyncVar[Int]
          a[NoSuchElementException] should be thrownBy sv.get()
        }
        "should become empty after `get`" in {
          val sv = new SyncVar[Int]
          sv.put(1).get()
          a[NoSuchElementException] should be thrownBy sv.get()
        }
      }

      "put" - {
        "should hold value" in {
          val sv = new SyncVar[Int]
          sv.put(1).get() shouldBe 1
        }
        "should throw exception when nonempty" in {
          val sv = new SyncVar[Int]
          sv.put(1)
          a[IllegalStateException] should be thrownBy sv.put(1)
        }
      }

      "getWait" - {
        "should work correctly" in {
          val sv = new SyncVar[Int]
          val startTime = System.currentTimeMillis()
          @volatile var delta = 0
          thread {
            val v = sv.getWait()
            v shouldBe 1
            delta = (System.currentTimeMillis() - startTime).toInt
          }
          Thread.sleep(100)
          sv.putWait(1)
          Thread.sleep(10)
          delta should be >= 100
        }
      }

      "putWait" - {
        "should work correctly" in {
          val sv = new SyncVar[Int]
          val startTime = System.currentTimeMillis()
          @volatile var delta = 0
          thread {
            sv.putWait(1).putWait(2)
            delta = (System.currentTimeMillis() - startTime).toInt
          }
          Thread.sleep(100)
          val v = sv.getWait()
          v shouldBe 1
          Thread.sleep(10)
          delta should be >= 100
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
          sv.put(1)
          sv.isEmpty shouldBe false
          sv.nonEmpty shouldBe true
        }
        "should be empty after `get`" in {
          val sv = new SyncVar[Int]
          sv.put(1).get()
          sv.isEmpty shouldBe true
          sv.nonEmpty shouldBe false
        }
      }

    }

  }

}
