package learning.airt.concurrency.chapter7

import scala.collection.generic._
import scala.collection.mutable
import scala.concurrent.stm._

class TArrayBuffer[A](initialSize: Int = 8)
  extends mutable.Buffer[A]
    with mutable.BufferLike[A, TArrayBuffer[A]]
    with GenericTraversableTemplate[A, TArrayBuffer]
    with mutable.Builder[A, TArrayBuffer[A]] {

  private val arrayRef = Ref[TArray[AnyRef]](TArray ofDim[AnyRef] initialSize)
  private val lengthRef = Ref(0)

  override def apply(n: Int): A = atomic { implicit txn =>
    withIndexChecked(n) {
      val array = arrayRef()
      array(n).asInstanceOf[A]
    }
  }

  override def update(n: Int, v: A): Unit = atomic { implicit txn =>
    withIndexChecked(n) {
      val array = arrayRef()
      array(n) = v.asInstanceOf[AnyRef]
    }
  }

  override def length: Int = lengthRef.single()

  override def +=(v: A): this.type = atomic { implicit txn =>
    ensureSize(lengthRef() + 1)
    val array = arrayRef()
    array(lengthRef()) = v.asInstanceOf[AnyRef]
    lengthRef() = lengthRef() + 1
    this
  }

  override def +=:(v: A): this.type = atomic { implicit txn =>
    ensureSize(lengthRef() + 1)
    shiftRight(0, 1, lengthRef() + 1)
    val array = arrayRef()
    array(0) = v.asInstanceOf[AnyRef]
    lengthRef() = lengthRef() + 1
    this
  }

  override def insertAll(n: Int, vs: Traversable[A]): Unit = atomic { implicit txn =>
    withIndexChecked(n) {
      ensureSize(lengthRef() + vs.size)
      shiftRight(n, vs.size, lengthRef() - n)
      val array = arrayRef()
      var i = 0
      for (v <- vs) {
        array(n + i) = v.asInstanceOf[AnyRef]
        i += 1
      }
      lengthRef() = lengthRef() + vs.size
    }
  }

  override def remove(n: Int): A = atomic { implicit txn =>
    withIndexChecked(n) {
      val array = arrayRef()
      val v = array(n)
      shiftLeft(n + 1, 1, lengthRef() - 1 - n)
      array(lengthRef() - 1) = null
      lengthRef() = lengthRef() - 1
      v.asInstanceOf[A]
    }
  }

  override def clear(): Unit = atomic { implicit txn =>
    arrayRef() = TArray ofDim[AnyRef] initialSize
    lengthRef() = 0
  }

  override def iterator: Iterator[A] = new TArrayBuffer.TArrayBufferIterator(this)

  override def companion: GenericCompanion[TArrayBuffer] = TArrayBuffer

  override def stringPrefix: String = "TArrayBuffer"

  override def result(): TArrayBuffer[A] = this

  override def sizeHint(size: Int): Unit = atomic { implicit txn =>
    if (arrayRef().length < size) {
      val oldArray = arrayRef()
      val newArray: TArray[AnyRef] = TArray ofDim[AnyRef] size
      for (i <- 0 until oldArray.length) newArray(i) = oldArray(i)
      arrayRef() = newArray
    }
  }

  protected def ensureSize(size: Int)(implicit txn: InTxn) {
    if (arrayRef().length < size) {
      var newSize = arrayRef().length * 2L
      while (newSize < size) newSize *= 2L
      if (newSize > Int.MaxValue) newSize = Int.MaxValue
      sizeHint(newSize.toInt)
    }
  }

  protected def withIndexChecked[B](n: Int)(action: => B)(implicit txn: InTxn): B = {
    if (n < 0 || n >= lengthRef()) throw new IndexOutOfBoundsException(n.toString) else {
      action
    }
  }

  protected def shiftLeft(index: Int, distance: Int, length: Int)(implicit txn: InTxn) {
    val array = arrayRef()
    for (i <- 0 until length) array(index + i - distance) = array(index + i)
  }

  protected def shiftRight(index: Int, distance: Int, length: Int)(implicit txn: InTxn) {
    val array = arrayRef()
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
