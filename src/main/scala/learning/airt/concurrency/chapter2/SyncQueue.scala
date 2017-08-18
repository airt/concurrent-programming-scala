package learning.airt.concurrency.chapter2

import scala.collection.mutable

class SyncQueue[A](n: Int) {

  private val variables = mutable.Queue[A]()

  // noinspection AccessorLikeMethodIsEmptyParen
  def getWait(): A = synchronized {
    while (variables.isEmpty) wait()
    val v = variables dequeue ()
    notify()
    v
  }

  def putWait(v: A): this.type = synchronized {
    while (variables.size == n) wait()
    variables += v
    notify()
    this
  }

}
