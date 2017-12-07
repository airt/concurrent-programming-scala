package learning.concurrent.programming.chapter5

import scala.collection.parallel._

class ParBinomialHeap[A: Ordering](heap: BinomialHeap[A]) extends immutable.ParIterable[A] {

  override def splitter: IterableSplitter[A] = new ParBinomialHeapSplitter(heap)

  override def seq: BinomialHeap[A] = heap

  override def size: Int = heap.size

  override def newCombiner = new ParBinomialHeapCombiner[A]

}

class ParBinomialHeapSplitter[A: Ordering](heap: BinomialHeap[A])
    extends BinomialHeap.BinomialHeapIterator[A](heap)
    with IterableSplitter[A] {

  override def dup: IterableSplitter[A] = new ParBinomialHeapSplitter(current)

  override def split: Seq[IterableSplitter[A]] = current.trees match {
    case tree :: Nil =>
      tree.split match {
        case Some((treeA, treeB)) => (treeA :: treeB :: Nil) map fromTree
        case None                 => Nil
      }
    case trees => trees map fromTree
  }

  override def remaining: Int = current.size

  private def fromTree(tree: BinomialTree[A]) =
    new ParBinomialHeapSplitter(new BinomialHeap(tree :: Nil))

}

class ParBinomialHeapCombiner[A: Ordering] extends Combiner[A, ParBinomialHeap[A]] {

  private var heap = BinomialHeap.empty[A]

  override def combine[N <: A, NewTo >: ParBinomialHeap[A]](
      other: Combiner[N, NewTo]
  ): Combiner[N, NewTo] = other match {
    case that if that eq this             => this
    case that: ParBinomialHeapCombiner[A] => heap = heap merge that.heap; this
    case _                                => throw new IllegalArgumentException
  }

  override def +=(value: A): this.type = {
    heap = heap insert value
    this
  }

  override def size: Int = heap.size

  override def clear(): Unit = heap = BinomialHeap.empty[A]

  override def result(): ParBinomialHeap[A] = heap.par

}
