package learning.airt.concurrency.chapter4

import resource.managed

import scala.concurrent._
import scala.concurrent.duration._

object Curl {

  import ExecutionContext.Implicits._

  def main(args: Array[String]) {
    apply(args(0), args contains "--silent")
  }

  def apply(url: String, silent: Boolean = false): Future[String] = {

    val print: Any => Unit = if (silent) _ => () else Predef.print

    val dotPrinter = DotPrinter start (500.millis, print)

    val res = Future {
      (managed(io.Source fromURL url) map (_.mkString)).opt.get
    } withTimeout 2.seconds

    res onComplete (_ => dotPrinter stop ())

    res foreach print

    res.failed foreach { e =>
      print(s"Exception: $e\n")
    }

    res
  }

  class DotPrinter(period: Duration, print: Any => Unit) {

    import java.util.{Timer, TimerTask}

    private val timer = new Timer

    private val task = new TimerTask {
      override def run(): Unit = print(".")
    }

    timer schedule (task, 0, period.toMillis)

    def stop() {
      print("\n")
      timer cancel ()
      timer purge ()
    }

  }

  object DotPrinter {

    def start(period: Duration, print: Any => Unit): DotPrinter = new DotPrinter(period, print)

  }

}
