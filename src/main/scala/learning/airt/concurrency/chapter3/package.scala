package learning.airt.concurrency

import scala.concurrent.ExecutionContext

package object chapter3 {

  def executes(task: => Unit): Unit = ExecutionContext.global.execute(() => task)

}
