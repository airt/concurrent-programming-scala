package learning.airt.concurrency.chapter4

import scala.concurrent._
import scala.sys.process._

object Spawn {

  def spawn(command: String)(implicit executor: ExecutionContext): Future[Int] =
    Future(blocking(command.!))

}
