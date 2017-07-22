package learning.airt.concurrency.chapter2

object Parallel {

  def parallel[A, B](taskA: => A)(taskB: => B): (A, B) = {
    var a: Option[A] = None
    var b: Option[B] = None
    val threadA = thread {
      a = Some(taskA)
    }
    val threadB = thread {
      b = Some(taskB)
    }
    threadA.join()
    threadB.join()
    (a.get, b.get)
  }

}
