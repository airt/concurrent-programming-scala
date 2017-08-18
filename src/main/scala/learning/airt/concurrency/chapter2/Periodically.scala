package learning.airt.concurrency.chapter2

import scala.concurrent.duration.Duration

object Periodically {

  def periodically(duration: Duration)(task: => Unit): Thread = {
    val worker = new Thread("periodically-worker") {
      override def run() {
        while (true) {
          task
          Thread sleep duration.toMillis
        }
      }
    }
    worker setDaemon true
    worker start ()
    worker
  }

}
