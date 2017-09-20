package learning.airt.concurrency.chapter2

object Parallel {

  def parallel[A, B](taskX: => A)(taskY: => B): (A, B) = {
    var x: Option[A] = None
    var y: Option[B] = None
    val tx = thread {
      x = Some(taskX)
    }
    val ty = thread {
      y = Some(taskY)
    }
    tx join ()
    ty join ()
    (x.get, y.get)
  }

}
