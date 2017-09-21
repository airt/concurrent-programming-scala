package learning.airt.concurrency.chapter5

import com.typesafe.scalalogging.LazyLogging

object TimedAllocating extends LazyLogging {

  def main(args: Array[String]) {
    val ns = Timer warmedTimed new AnyRef
    logger debug s"the average running time of allocating a simple object is $ns nanoseconds"
  }

}

object Timer {

  /**
    * @return nanoseconds
    */
  def warmedTimed[A](body: => A, runningTimes: Int = 10000000, preRunningTimes: Int = 10000000): Long = {
    run(body, preRunningTimes)
    timed(body, runningTimes)
  }

  /**
    * @return nanoseconds
    */
  def timed[A](body: => A, runningTimes: Int = 1): Long = {
    val start = System nanoTime ()
    run(body, runningTimes)
    val stop = System nanoTime ()
    (stop - start) / runningTimes
  }

  @inline
  private def run[A](body: => A, times: Int) {
    var i = 0
    while (i < times) {
      dummy = body
      i += 1
    }
  }

  private var dummy: Any = _

}
