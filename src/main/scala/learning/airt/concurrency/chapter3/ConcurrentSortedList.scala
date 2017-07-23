package learning.airt.concurrency.chapter3

import java.util.concurrent.atomic.AtomicReference

import scala.annotation.tailrec

class ConcurrentSortedList[A](implicit val ord: Ordering[A]) {

  def add(v: A) {
    addTo(rr, v)
  }

  @tailrec
  private def addTo(vsr: ListAtomicRef[A], v: A) {
    vsr.get() match {
      case vs@CCons(h, t) =>
        if (ord.lteq(v, h)) {
          if (!vsr.compareAndSet(vs, CCons(v, newListAtomicRef(vs)))) addTo(vsr, v)
        } else {
          addTo(t, v)
        }
      case vs@CNil =>
        if (!vsr.compareAndSet(vs, CCons(v, newListAtomicRef(vs)))) addTo(vsr, v)
    }
  }

  def iterator: Iterator[A] = new Iterator[A] {
    private var current = rr.get()

    override def hasNext: Boolean = current != CNil

    override def next(): A = current match {
      case CCons(h, t) => current = t.get(); h
      case CNil => Iterator.empty.next()
    }
  }

  private val rr = newListAtomicRef(CNil)

  private type ListAtomicRef[E] = AtomicReference[CList[E]]

  private trait CList[+E]

  private case object CNil extends CList[Nothing]

  private case class CCons[E](h: E, t: ListAtomicRef[E]) extends CList[E]

  private def newListAtomicRef(vs: CList[A]) = new AtomicReference[CList[A]](vs)

}
