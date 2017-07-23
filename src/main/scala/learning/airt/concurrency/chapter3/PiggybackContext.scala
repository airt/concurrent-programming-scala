package learning.airt.concurrency.chapter3

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

class PiggybackContext extends ExecutionContext with LazyLogging {

  override def execute(task: Runnable) {
    try task.run() catch {
      case NonFatal(e) => reportFailure(e)
    }
  }

  override def reportFailure(cause: Throwable) {
    logger.warn(s"piggyback task exception: ${cause.getMessage}")
  }

}
