package learning.airt.concurrency.chapter7

import scala.collection.generic._
import scala.collection.mutable
import scala.concurrent.stm._

class TArrayBuffer[A](initialSize: Int = 8)
  extends mutable.Buffer[A]
    with mutable.BufferLike[A, TArrayBuffer[A]]
    with GenericTraversableTemplate[A, TArrayBuffer]
    with mutable.Builder[A, TArrayBuffer[A]] {

  private val ar = Ref[TArray[AnyRef]](TArray ofDim[AnyRef] initialSize)
  private val alr = Ref(0)

  override def apply(n: Int): A = atomic { implicit txn =>
    withCheckedIndex(n) {
      val array = ar()
      array(n).asInstanceOf[A]
    }
  }

  override def update(n: Int, v: A): Unit = atomic { implicit txn =>
    withCheckedIndex(n) {
      val array = ar()
      array(n) = v.asInstanceOf[AnyRef]
    }
  }

  override def length: Int = alr.single()

  override def +=(v: A): this.type = atomic { implicit txn =>
    alr() = alr() + 1
    ensureSize(alr())
    val array = ar()
    array(alr() - 1) = v.asInstanceOf[AnyRef]
    this
  }

  override def +=:(v: A): this.type = atomic { implicit txn =>
    alr() = alr() + 1
    ensureSize(alr())
    shiftRight(0, 1, alr())
    val array = ar()
    array(0) = v.asInstanceOf[AnyRef]
    this
  }

  override def insertAll(n: Int, vs: Traversable[A]): Unit = atomic { implicit txn =>
    withCheckedIndex(n) {
      alr() = alr() + vs.size
      ensureSize(alr())
      shiftRight(n, vs.size, alr() - n)
      val array = ar()
      var i = 0
      for (v <- vs) {
        array(n + i) = v.asInstanceOf[AnyRef]
        i += 1
      }
    }
  }

  override def remove(n: Int): A = atomic { implicit txn =>
    withCheckedIndex(n) {
      alr() = alr() - 1
      val array = ar()
      val v = array(n)
      shiftLeft(n + 1, 1, alr() - n)
      array(alr()) = null
      v.asInstanceOf[A]
    }
  }

  override def clear(): Unit = atomic { implicit txn =>
    ar() = TArray ofDim[AnyRef] initialSize
    alr() = 0
  }

  override def iterator: Iterator[A] = new TArrayBuffer.TArrayBufferIterator(this)

  override def companion: GenericCompanion[TArrayBuffer] = TArrayBuffer

  override def stringPrefix: String = "TArrayBuffer"

  override def result(): TArrayBuffer[A] = this

  override def sizeHint(size: Int): Unit = atomic { implicit txn =>
    if (size > length) {
      val oldArray = ar()
      val newArray: TArray[AnyRef] = TArray ofDim[AnyRef] size
      for (i <- 0 until oldArray.length) newArray(i) = oldArray(i)
      ar() = newArray
    }
  }

  protected def ensureSize(size: Int)(implicit txn: InTxn) {
    if (ar().length < size) {
      var newSize = ar().length * 2L
      while (newSize < size) newSize *= 2L
      if (newSize > Int.MaxValue) newSize = Int.MaxValue
      sizeHint(newSize.toInt)
    }
  }

  protected def withCheckedIndex[B](n: Int)(action: => B)(implicit txn: InTxn): B = {
    if (n < 0 || n >= alr()) throw new IndexOutOfBoundsException(n.toString) else {
      action
    }
  }

  protected def shiftLeft(index: Int, distance: Int, length: Int)(implicit txn: InTxn) {
    val array = ar()
    for (i <- 0 until length) array(index + i - distance) = array(index + i)
  }

  protected def shiftRight(index: Int, distance: Int, length: Int)(implicit txn: InTxn) {
    val array = ar()
    for (i <- (length - 1) to 0 by (-1)) array(index + i + distance) = array(index + i)
  }

}

object TArrayBuffer extends SeqFactory[TArrayBuffer] {

  override def newBuilder[A] = new TArrayBuffer[A]

  class TArrayBufferIterator[A](buffer: TArrayBuffer[A]) extends Iterator[A] {

    private var currentIndex = 0

    override def hasNext: Boolean = currentIndex < buffer.length

    override def next(): A = if (!hasNext) Iterator.empty.next() else {
      val v = buffer(currentIndex)
      currentIndex += 1
      v
    }

  }

}
