package learning.airt.concurrency.chapter2

import scala.collection.mutable

class PriorityTaskPool(workersQuantity: Int = 1, importantPriority: Int = 0) {

  private implicit val taskOrdering: Ordering[PriorityTask] = Ordering.by(_.priority)

  private val tasks = mutable.PriorityQueue[PriorityTask]()

  private val workers = (1 to workersQuantity) map (new Worker(_))

  @volatile private var terminated = false

  def asynchronous(priority: Int)(task: => Unit) {
    tasks synchronized {
      tasks.enqueue(PriorityTask(priority, () => task))
      tasks.notify()
    }
  }

  def shutdown() {
    tasks synchronized {
      terminated = true
      tasks.notify()
    }
  }

  private case class PriorityTask(priority: Int, task: () => Unit)

  private class Worker(id: Int) extends Thread {
    setName(s"priority-task-pool-worker-$id")
    setDaemon(true)

    override def run() {
      while (true) {
        val PriorityTask(priority, task) = poll()
        if (!terminated || priority >= importantPriority) task()
      }
    }

    def poll(): PriorityTask = tasks synchronized {
      while (tasks.isEmpty) tasks.wait()
      tasks.dequeue()
    }
  }

  workers foreach (_.start())

}
