package learning.airt.concurrency.chapter3

import java.util.concurrent.atomic._

import com.typesafe.scalalogging.LazyLogging
import org.scalatest._

import scala.collection.JavaConverters._
import scala.util._

class ExercisesSpec extends FreeSpec with Matchers with Inspectors with LazyLogging {

  import Exercises._

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

    "TreiberStack" - {
      "should work correctly" in {
        val stack = new TreiberStack[Int]
        val rs = new java.util.concurrent.LinkedBlockingQueue[Int]
        (1 to 100) foreach { i =>
          executes {
            stack push i
          }
        }
        Thread.sleep(10)
        (1 to 100) foreach { _ =>
          executes {
            rs put stack.pop()
          }
        }
        Thread.sleep(10)
        rs.asScala.toSeq.sorted shouldEqual (1 to 100)
      }
    }

    "LazyCell" - {
      "should initialize after get" in {
        val initialized = new AtomicBoolean(false)
        val cell = LazyCell {
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
        val cell = LazyCell {
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
    }

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
        (1 to 100) foreach { _ => cell() }
        Thread.sleep(100)
        count.get() should be <= 100
        logger.debug(s"PureLazyCell initialization count ${count.get()}")
      }
    }

    "SyncConcurrentMap" - {
      "putIfAbsent" - {
        "should put key value and return `None` when not exist" in {
          val m = SyncConcurrentMap[Int, Int]()
          (1 to 100) foreach { i =>
            executes {
              m.putIfAbsent(i, 1) shouldBe None
            }
          }
          Thread.sleep(10)
          m should have size 100
          forAll(m.values)(_ shouldBe 1)
        }
        "should not put key value and return `Some(ov)` when exist" in {
          val m = SyncConcurrentMap[Int, Int]((1 to 100) map ((_, 0)): _*)
          (1 to 100) foreach { i =>
            executes {
              m.putIfAbsent(i, 1) shouldBe Some(0)
            }
          }
          Thread.sleep(10)
          m should have size 100
          forAll(m.values)(_ shouldBe 0)
        }
      }
      "remove" - {
        "should remove key value and return true when exist and equal" in {
          val m = SyncConcurrentMap[Int, Int]((1 to 100) map ((_, 0)): _*)
          (1 to 100) foreach { i =>
            executes {
              m.remove(i, 0) shouldBe true
            }
          }
          Thread.sleep(10)
          m should have size 0
        }
        "should not remove key value and return false when exist but not equal" in {
          val m = SyncConcurrentMap[Int, Int]((1 to 100) map ((_, 0)): _*)
          (1 to 100) foreach { i =>
            executes {
              m.remove(i, 1) shouldBe false
            }
          }
          Thread.sleep(10)
          m should have size 100
        }
        "should not remove key value and return false when not exist" in {
          val m = SyncConcurrentMap[Int, Int]()
          (1 to 100) foreach { i =>
            executes {
              m.remove(i, 1) shouldBe false
            }
          }
          Thread.sleep(10)
          m should have size 0
        }
      }
      "replace" - {
        "should put value and return `Some(ov)` when exist" in {
          val m = SyncConcurrentMap[Int, Int]((1 to 100) map ((_, 0)): _*)
          (1 to 100) foreach { i =>
            executes {
              m.replace(i, 1) shouldBe Some(0)
            }
          }
          Thread.sleep(10)
          m should have size 100
          forAll(m.values)(_ shouldBe 1)
        }
        "should not put value and return `None` when not exist" in {
          val m = SyncConcurrentMap[Int, Int]()
          (1 to 100) foreach { i =>
            executes {
              m.replace(i, 1) shouldBe None
            }
          }
          Thread.sleep(10)
          m should have size 0
        }
      }
      "replace" - {
        "should put value and return true when exist and equal" in {
          val m = SyncConcurrentMap[Int, Int]((1 to 100) map ((_, 0)): _*)
          (1 to 100) foreach { i =>
            executes {
              m.replace(i, 0, 1) shouldBe true
            }
          }
          Thread.sleep(10)
          m should have size 100
          forAll(m.values)(_ shouldBe 1)
        }
        "should not put value and return false when exist but not equal" in {
          val m = SyncConcurrentMap[Int, Int]((1 to 100) map ((_, 0)): _*)
          (1 to 100) foreach { i =>
            executes {
              m.replace(i, 2, 1) shouldBe false
            }
          }
          Thread.sleep(10)
          m should have size 100
          forAll(m.values)(_ shouldBe 0)
        }
        "should not put value and return false when not exist" in {
          val m = SyncConcurrentMap[Int, Int]().empty
          (1 to 100) foreach { i =>
            executes {
              m.replace(i, 2, 1) shouldBe false
            }
          }
          Thread.sleep(10)
          m should have size 0
        }
      }
    }

    "spawn" - {
      "should work correctly" in {
        val x = Random.nextInt()
        val y = Random.nextInt()
        val result = spawn {
          x + y
        }
        result shouldBe Success(x + y)
      }
    }

  }

}