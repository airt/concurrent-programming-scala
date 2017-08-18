package learning.airt.concurrency.chapter3

import learning.airt.concurrency.chapter2.thread
import org.scalatest._

class SyncConcurrentMapSpec extends FreeSpec with Matchers with Inspectors {

  "Exercises in Chapter 3" - {

    "SyncConcurrentMap" - {
      "putIfAbsent" - {
        "should put key value and return `None` when not exist" in {
          val m = SyncConcurrentMap[Int, Int]()
          (1 to 100) map { i =>
            thread {
              m putIfAbsent (i, 1) shouldBe None
            }
          } foreach (_ join ())
          m should have size 100
          forAll(m.values)(_ shouldBe 1)
        }
        "should not put key value and return `Some(ov)` when exist" in {
          val m = SyncConcurrentMap[Int, Int]((1 to 100) map ((_, 0)): _*)
          (1 to 100) map { i =>
            thread {
              m putIfAbsent (i, 1) shouldBe Some(0)
            }
          } foreach (_ join ())
          m should have size 100
          forAll(m.values)(_ shouldBe 0)
        }
      }
      "remove" - {
        "should remove key value and return true when exist and equal" in {
          val m = SyncConcurrentMap[Int, Int]((1 to 100) map ((_, 0)): _*)
          (1 to 100) map { i =>
            thread {
              m remove (i, 0) shouldBe true
            }
          } foreach (_ join ())
          m should have size 0
        }
        "should not remove key value and return false when exist but not equal" in {
          val m = SyncConcurrentMap[Int, Int]((1 to 100) map ((_, 0)): _*)
          (1 to 100) map { i =>
            thread {
              m remove (i, 1) shouldBe false
            }
          } foreach (_ join ())
          m should have size 100
        }
        "should not remove key value and return false when not exist" in {
          val m = SyncConcurrentMap[Int, Int]()
          (1 to 100) map { i =>
            thread {
              m remove (i, 1) shouldBe false
            }
          } foreach (_ join ())
          m should have size 0
        }
      }
      "replace" - {
        "should put value and return `Some(ov)` when exist" in {
          val m = SyncConcurrentMap[Int, Int]((1 to 100) map ((_, 0)): _*)
          (1 to 100) map { i =>
            thread {
              m replace (i, 1) shouldBe Some(0)
            }
          } foreach (_ join ())
          m should have size 100
          forAll(m.values)(_ shouldBe 1)
        }
        "should not put value and return `None` when not exist" in {
          val m = SyncConcurrentMap[Int, Int]()
          (1 to 100) map { i =>
            thread {
              m replace (i, 1) shouldBe None
            }
          } foreach (_ join ())
          m should have size 0
        }
      }
      "replace" - {
        "should put value and return true when exist and equal" in {
          val m = SyncConcurrentMap[Int, Int]((1 to 100) map ((_, 0)): _*)
          (1 to 100) map { i =>
            thread {
              m replace (i, 0, 1) shouldBe true
            }
          } foreach (_ join ())
          m should have size 100
          forAll(m.values)(_ shouldBe 1)
        }
        "should not put value and return false when exist but not equal" in {
          val m = SyncConcurrentMap[Int, Int]((1 to 100) map ((_, 0)): _*)
          (1 to 100) map { i =>
            thread {
              m replace (i, 2, 1) shouldBe false
            }
          } foreach (_ join ())
          m should have size 100
          forAll(m.values)(_ shouldBe 0)
        }
        "should not put value and return false when not exist" in {
          val m = SyncConcurrentMap[Int, Int]().empty
          (1 to 100) map { i =>
            thread {
              m replace (i, 2, 1) shouldBe false
            }
          } foreach (_ join ())
          m should have size 0
        }
      }
    }

  }

}
