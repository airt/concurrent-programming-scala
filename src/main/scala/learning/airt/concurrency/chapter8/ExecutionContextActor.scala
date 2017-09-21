package learning.airt.concurrency.chapter8

import akka.actor._
import com.typesafe.scalalogging.LazyLogging

import scala.collection.mutable
import scala.util.control.NonFatal

class ExecutionContextActor(nWorkers: Int, reportFailure: Throwable => Unit) extends Actor {

  import ExecutionContextActor._

  private val tasks = mutable.Queue[Execute]()
  private val workers = mutable.Queue[ActorRef]()
  private val runningWorkers = mutable.Set[ActorRef]()

  def receive: Receive = {
    case task: Execute =>
      tasks enqueue task
      dispatch()
    case Finished =>
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
    }
  }

  override def preStart() {
    (1 to nWorkers) foreach { i =>
      workers enqueue (context actorOf (Props[WorkerActor], s"worker-$i"))
    }
  }

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
    case NonFatal(e) =>
      reportFailure(e)
      SupervisorStrategy.Resume
  }

}

object ExecutionContextActor extends LazyLogging {

  def props(
      nWorkers: Int = Runtime.getRuntime.availableProcessors,
      reportFailure: Throwable => Unit = e => logger warn s"$e",
  ) = Props(new ExecutionContextActor(nWorkers, reportFailure))

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
