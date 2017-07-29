package learning.airt.concurrency.chapter5

import scala.collection.parallel._

class ParBinomialHeap[A: Ordering](heap: BinomialHeap[A])
  extends immutable.ParIterable[A] {

  override def splitter: IterableSplitter[A] = new ParBinomialHeapSplitter(heap)

  override def seq: BinomialHeap[A] = heap

  override def size: Int = heap.size

  override def stringPrefix: String = "ParBinomialHeap"

}

class ParBinomialHeapSplitter[A: Ordering](heap: BinomialHeap[A])
  extends BinomialHeap.BinomialHeapIterator[A](heap)
    with IterableSplitter[A] {

  override def dup: IterableSplitter[A] = new ParBinomialHeapSplitter(current)

  override def split: Seq[IterableSplitter[A]] = current.trees match {
    case tree :: Nil => tree.split match {
      case Some((treeA, treeB)) => (treeA :: treeB :: Nil) map fromTree
      case None => Nil
    }
    case trees => trees map fromTree
  }

  override def remaining: Int = current.size

  private def fromTree(tree: BinomialTree[A]) =
    new ParBinomialHeapSplitter(new BinomialHeap(tree :: Nil))

}
