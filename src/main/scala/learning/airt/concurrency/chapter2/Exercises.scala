package learning.airt.concurrency.chapter2

import com.typesafe.scalalogging.LazyLogging

import scala.collection.mutable
import scala.concurrent.duration.Duration

object Exercises extends LazyLogging {

  def parallel[A, B](taskA: => A)(taskB: => B): (A, B) = {
    var a: Option[A] = None
    var b: Option[B] = None
    val threadA = thread {
      a = Some(taskA)
    }
    val threadB = thread {
      b = Some(taskB)
    }
    threadA.join()
    threadB.join()
    (a.get, b.get)
  }

  def periodically(duration: Duration)(task: => Unit): Thread = {
    val worker = new Thread("periodically-worker") {
      override def run() {
        while (true) {
          task
          Thread.sleep(duration.toMillis)
        }
      }
    }
    worker.setDaemon(true)
    worker.start()
    worker
  }

  def runProducerAndConsumer() {
    val sv = new SyncVar[Int]

    val consumer = thread {
      var v = 0
      while (v != 15) {
        if (sv.nonEmpty) {
          v = sv.get()
        } else Thread.`yield`()
      }
      logger.debug(s"consumer task completed")
    }

    val producer = thread {
      var v = 0
      while (v <= 15) {
        if (sv.isEmpty) {
          sv.put(v)
          v += 1
        } else Thread.`yield`()
      }
      logger.debug(s"producer task completed")
    }

    producer.join()
    consumer.join()
  }

  def runProducerAndConsumerWithWait() {
    val sv = new SyncVar[Int]

    val consumer = thread {
      var v = 0
      while (v != 15) {
        v = sv.getWait()
      }
      logger.debug(s"consumer task with wait completed")
    }

    val producer = thread {
      var v = 0
      while (v <= 15) {
        sv.putWait(v)
        v += 1
      }
      logger.debug(s"producer task with wait completed")
    }

    producer.join()
    consumer.join()
  }

  def runProducerAndConsumerWithQueue() {
    val sq = new SyncQueue[Int](5)

    val consumer = thread {
      var v = 0
      while (v != 15) {
        v = sq.getWait()
      }
      logger.debug(s"consumer task with queue completed")
    }

    val producer = thread {
      var v = 0
      while (v <= 15) {
        sq.putWait(v)
        v += 1
      }
      logger.debug(s"producer task with queue completed")
    }

    producer.join()
    consumer.join()
  }

  def send(sender: Account, target: Account, amount: Int) {
    def adjust() {
      sender.money -= amount
      target.money += amount
    }

    val Seq(first, second) = Seq(sender, target).sortBy(_.id)

    first synchronized {
      second synchronized {
        adjust()
      }
    }
  }

  def sendAll(senders: Set[Account], target: Account) {
    def adjust() {
      target.money += (0 /: senders) { (total, sender) =>
        val money = sender.money
        sender.money = 0
        total + money
      }
    }

    def synchronizedAll[R](task: () => R): List[AnyRef] => R = {
      case x :: xs => x synchronized synchronizedAll(task)(xs)
      case _ => task()
    }

    synchronizedAll(() => adjust())((target :: senders.toList) sortBy (_.id))
  }

  class SyncVar[T] {
    private var variable: Option[T] = None

    def isEmpty: Boolean = synchronized(variable.isEmpty)

    def nonEmpty: Boolean = synchronized(variable.nonEmpty)

    def get(): T = synchronized {
      variable match {
        case Some(v) => variable = None; v
        case None => throw new NoSuchElementException("get from empty variable")
      }
    }

    def put(v: T): this.type = synchronized {
      variable match {
        case Some(_) => throw new IllegalStateException("put to nonempty variable")
        case None => variable = Some(v); this
      }
    }

    // noinspection AccessorLikeMethodIsEmptyParen
    def getWait(): T = synchronized {
      while (variable.isEmpty) wait()
      val Some(v) = variable
      variable = None
      notify()
      v
    }

    def putWait(v: T): this.type = synchronized {
      while (variable.nonEmpty) wait()
      variable = Some(v)
      notify()
      this
    }
  }

  class SyncQueue[T](n: Int) {
    private val variables = mutable.Queue[T]()

    // noinspection AccessorLikeMethodIsEmptyParen
    def getWait(): T = synchronized {
      while (variables.isEmpty) wait()
      val v = variables.dequeue()
      notify()
      v
    }

    def putWait(v: T): this.type = synchronized {
      while (variables.size == n) wait()
      variables += v
      notify()
      this
    }
  }

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

}

object SynchronizedProtectedUid {
  private var uid = 0L

  def next(): Long = synchronized {
    uid += 1
    uid
  }
}

case class Account(id: Long, name: String, var money: Int)

object Account {
  def apply(name: String, money: Int): Account =
    apply(SynchronizedProtectedUid.next(), name, money)
}
