package learning.airt.concurrency.chapter4

import java.util.{Timer, TimerTask}

import resource.managed

import scala.concurrent._

object Curl {

  import ExecutionContext.Implicits.global

  def main(args: Array[String]) {
    apply(args(0), args contains "--silent")
  }

  def apply(url: String, silent: Boolean = false): Future[String] = {

    val print: Any => Unit = if (silent) _ => () else Predef.print

    val dotPrinter = DotPrinter start (500, print)

    val res = Future {
      (managed(io.Source fromURL url) map (_.mkString)).opt.get
    } withTimeout 2000

    res onComplete (_ => dotPrinter stop ())

    res foreach print

    res.failed foreach { e =>
      print(s"Exception: $e\n")
    }

    res
  }

  class DotPrinter(period: Long, print: Any => Unit) {

    private val timer = new Timer

    private val task = new TimerTask {
      override def run(): Unit = print(".")
    }

    timer schedule (task, 0, period)

    def stop() {
      print("\n")
      timer cancel ()
      timer purge ()
    }

  }

  object DotPrinter {

    def start(period: Long, print: Any => Unit): DotPrinter = new DotPrinter(period, print)

  }

}
