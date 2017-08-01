package learning.airt.concurrency.chapter6

import rx.lang.scala.Observable

import scala.annotation.tailrec

object NewThreadsObservable {

  def apply(): Observable[Thread] =
    Observable[Thread] { subscriber =>
      val previousThreads = threads
      threads = currentThreads()
      threads &~ previousThreads foreach subscriber.onNext
      subscriber.onCompleted()
    }

  private var threads = currentThreads()

  private def currentThreads(): Set[Thread] = {
    val root = rootThreadGroup()
    val activeThreads = Array.ofDim[Thread](root.activeCount)
    root.enumerate(activeThreads, true)
    activeThreads.toSet filter (_ != null)
  }

  private def rootThreadGroup(): ThreadGroup =
    rootThreadGroupOf(Thread.currentThread.getThreadGroup)

  @tailrec
  private def rootThreadGroupOf(threadGroup: ThreadGroup): ThreadGroup =
    Option(threadGroup.getParent) match {
      case Some(parent) => rootThreadGroupOf(parent)
      case None => threadGroup
    }

}
