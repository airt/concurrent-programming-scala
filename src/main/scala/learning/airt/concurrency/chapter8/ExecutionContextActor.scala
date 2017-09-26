package learning.airt.concurrency.chapter8

import akka.actor._

import scala.collection.mutable
import scala.util.control.NonFatal

class ExecutionContextActor(nWorkers: Int) extends Actor with ActorLogging {

  import ExecutionContextActor._

  private val tasks = mutable.Queue[Execute]()
  private val workers = mutable.Queue[ActorRef]()
  private val runningWorkers = mutable.Set[ActorRef]()

  def receive: Receive = {
    case task: Execute =>
      log debug s"receive task $task"
      tasks enqueue task
      dispatch()
    case Finished =>
      log debug s"$sender finished"
      runningWorkers remove sender()
      workers enqueue sender()
      dispatch()
  }

  private def dispatch() {
    if (tasks.nonEmpty && workers.nonEmpty) {
      val task = tasks dequeue ()
      val worker = workers dequeue ()
      runningWorkers add worker
      worker ! task
      log debug s"$worker start"
    }
  }

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
    case NonFatal(e) =>
      log debug s"$sender failed"
      runningWorkers remove sender()
      workers enqueue sender()
      dispatch()
      reportFailure(e)
      SupervisorStrategy.Resume
  }

  override def preStart() {
    (1 to nWorkers) foreach { i =>
      workers enqueue (context actorOf (Props[WorkerActor], s"worker-$i"))
    }
    log debug s"pre start / workers.size = ${workers.size}"
  }

  override def postStop() {
    log debug s"post stop / workers.size = ${workers.size}"
  }

  protected def reportFailure(cause: Throwable): Unit = log warning s"$cause"

}

object ExecutionContextActor {

  def props(nWorkers: Int = availableProcessors) = Props(new ExecutionContextActor(nWorkers))

  case class Execute(task: Runnable)

  case object Finished

  class WorkerActor extends Actor {

    def receive: Receive = {
      case Execute(task) =>
        task run ()
        sender() ! Finished
    }

  }

}
