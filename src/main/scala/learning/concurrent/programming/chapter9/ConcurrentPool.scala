package learning.concurrent.programming.chapter9

trait ConcurrentPool[A] {

  def add(x: A): Unit

  def remove(): A

  def isEmpty: Boolean

}
