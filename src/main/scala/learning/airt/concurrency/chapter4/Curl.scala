package learning.airt.concurrency.chapter4

import java.util.{Timer, TimerTask}

import scala.concurrent._

object Curl {

  import ExecutionContext.Implicits.global

  def main(args: Array[String]) {
    apply(args.head)
  }

  def apply(url: String): Future[String] = {
    val dotPrinter = DotPrinter.start(500)

    val res = Future {
      io.Source.fromURL(url).mkString
    } withTimeout 2000

    res onComplete (_ => dotPrinter.stop())

    res
  }

  class DotPrinter(period: Long) {
    private val timer = new Timer

    timer.schedule(new TimerTask {
      override def run(): Unit = print(".")
    }, 0, period)

    def stop() {
      println()
      timer.cancel()
      timer.purge()
    }
  }

  object DotPrinter {
    def start(period: Long): DotPrinter = new DotPrinter(period)
  }

}
