package learning.airt.concurrency.chapter2

import com.typesafe.scalalogging.LazyLogging
import org.scalatest._

class ExercisesSpec extends FreeSpec with Matchers with Inspectors with LazyLogging {

  import Exercises._

  "Exercises in Chapter 2" - {

    "parallel" - {
      "should work correctly" in {
        parallel(1)(2) shouldBe(1, 2)
      }
    }

    "periodically" - {
      "should work correctly" in {
        import scala.concurrent.duration._
        var i = 0
        periodically(1.millisecond) {
          i += 1
        }
        Thread.sleep(10)
        i should be > 0
      }
    }

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

    "SyncQueue" - {

      "getWait" - {
        "should work correctly" in {
          val sq = new SyncQueue[Int](2)
          val startTime = System.currentTimeMillis()
          @volatile var delta = 0
          thread {
            val v = sq.getWait()
            v shouldBe 1
            delta = (System.currentTimeMillis() - startTime).toInt
          }
          Thread.sleep(100)
          sq.putWait(1)
          Thread.sleep(10)
          delta should be >= 100
        }
      }

      "putWait" - {
        "should work correctly" in {
          val sq = new SyncQueue[Int](2)
          val startTime = System.currentTimeMillis()
          @volatile var delta = 0
          thread {
            sq.putWait(1).putWait(2).putWait(3)
            delta = (System.currentTimeMillis() - startTime).toInt
          }
          Thread.sleep(100)
          val v = sq.getWait()
          v shouldBe 1
          Thread.sleep(10)
          delta should be >= 100
        }
      }

    }

    "runProducerAndConsumer" - {
      "should run correctly" in {
        noException should be thrownBy runProducerAndConsumer()
      }
    }

    "runProducerAndConsumerWithWait" - {
      "should run correctly" in {
        noException should be thrownBy runProducerAndConsumerWithWait()
      }
    }

    "runProducerAndConsumerWithQueue" - {
      "should run correctly" in {
        noException should be thrownBy runProducerAndConsumerWithQueue()
      }
    }

    "send" - {
      "should work correctly" in {
        val a = Account("A", 100)
        val b = Account("B", 100)
        (1 to 100) foreach { _ =>
          thread {
            send(a, b, 10)
          }
          thread {
            send(b, a, 10)
          }
        }
        forAll(Seq(a, b))(_.money shouldBe 100)
      }
    }

    "sendAll" - {
      "should work correctly" in {
        val senders = Set(Account("A", 100), Account("B", 200), Account("C", 300))
        val target = Account("Z", 1000)
        sendAll(senders, target)
        forAll(senders)(_.money shouldBe 0)
        target.money shouldBe 1600
      }
    }

    "PriorityTaskPool" - {
      "should work correctly" in {
        val tasksQuantity = 100
        val workersQuantity = 2
        val importantPriority = 90

        val pool = new PriorityTaskPool(workersQuantity, importantPriority)

        case class PriorityTaskRef(priority: Int, id: Int)
        var executedTasks = List[PriorityTaskRef]()
        val lock = new AnyRef

        (1 to tasksQuantity) foreach { id =>
          val priority = (math.random() * tasksQuantity).toInt
          (pool asynchronous priority) {
            Thread.sleep(1)
            lock synchronized (executedTasks ::= PriorityTaskRef(priority, id))
          }
        }

        pool.shutdown()
        Thread.sleep(100)

        executedTasks.size should be < tasksQuantity
        val executedTasksPriority = executedTasks filter (_.id > workersQuantity * 2) map (_.priority)
        all(executedTasksPriority) should be >= importantPriority
      }
    }

  }

}
