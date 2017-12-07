package learning.concurrent.programming.chapter9

import org.scalameter.api._

object STMConcurrentPoolBench extends Bench.LocalTime {

  private val sizes = (Gen range "size")(20000, 100000, 20000)

  performance of "STMConcurrentPool" in {

    measure method "add & remove" in {

      val pool = STMConcurrentPool[Int]()

      using(sizes) in { n =>
        (1 to n).par foreach (_ => pool add 0)
        (1 to (n * 2)).par foreach { i =>
          if (i % 2 == 0)
            pool add i
          else
            pool remove ()
        }
        (1 to n).par foreach (_ => pool remove ())
      }

    }

  }

}
