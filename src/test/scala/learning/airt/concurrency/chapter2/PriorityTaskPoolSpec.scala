package learning.airt.concurrency.chapter2

import org.scalatest._

class PriorityTaskPoolSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 2" - {

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

        val executedTasksPriorities =
          executedTasks filter (_.id > workersQuantity * 2) map (_.priority)

        all(executedTasksPriorities) should be >= importantPriority
      }
    }

  }

}
