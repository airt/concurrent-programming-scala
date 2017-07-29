package learning.airt.concurrency.chapter5

import learning.airt.concurrency.chapter5.BinomialHeap.BinomialTrees

/**
  * @param trees should be ascending by `tree.value`
  */
class BinomialHeap[A: Ordering](val trees: BinomialTrees[A])
  extends collection.immutable.Iterable[A] {

  def insert(value: A): BinomialHeap[A] =
    this merge new BinomialHeap((BinomialTree zero value) :: Nil)

  def remove: (A, BinomialHeap[A]) = {
    import BinomialHeap.ListOps
    val (tree, treesR) = trees removeMinBy (_.value)
    val heapN = new BinomialHeap(treesR) merge new BinomialHeap(tree.children.reverse)
    (tree.value, heapN)
  }

  def minimum: A = (trees map (_.value)).min

  def merge(rhs: BinomialHeap[A]): BinomialHeap[A] = {
    import BinomialHeap.BinomialTreesOps
    new BinomialHeap(trees merge rhs.trees)
  }

  override def size: Int = (trees map (_.size)).sum

  override def isEmpty: Boolean = trees.isEmpty

  override def iterator: Iterator[A] = new BinomialHeap.BinomialHeapIterator(this)

  override def par = new ParBinomialHeap(this)

  def show: String = showLines mkString "\n"

  def showLines: Seq[String] =
    "BinomialHeap {" +: (trees map (_.showLines) reduce (_ ++ Seq(",") ++ _)) :+ "}"

}

object BinomialHeap {

  type BinomialTrees[A] = List[BinomialTree[A]]

  def apply[A: Ordering](xs: A*): BinomialHeap[A] = (empty[A] /: xs) (_ insert _)

  def empty[A: Ordering] = new BinomialHeap[A](Nil)

  def from[A: Ordering](xs: Seq[A]): BinomialHeap[A] = apply[A](xs: _*)

  implicit class ListOps[A](private val xs: List[A]) extends AnyVal {

    def removeMinBy[B](f: A => B)(implicit ord: Ordering[B]): (A, List[A]) = {
      if (xs.isEmpty) throw new UnsupportedOperationException("empty.removeMinBy")
      val (_, m, rs) =
        (xs.tail :\ (f(xs.head), xs.head, Nil: List[A])) { (x, z) =>
          val (fm, m, rs) = z
          val fx = f(x)
          if (ord.lt(fx, fm))
            (fx, x, m :: rs)
          else
            (fm, m, x :: rs)
        }
      (m, rs)
    }

  }

  implicit class BinomialTreesOps[A](private val trees: BinomialTrees[A]) extends AnyVal {

    def merge(rhs: BinomialTrees[A])(implicit ord: Ordering[A]): BinomialTrees[A] =
      mergeTrees(mergeAsc(trees, rhs))

    def mergeAsc(xs: BinomialTrees[A], ys: BinomialTrees[A]): BinomialTrees[A] = (xs, ys) match {
      case (Nil, _) => ys
      case (_, Nil) => xs
      case (x :: xst, y :: yst) =>
        if (x.order <= y.order)
          x :: mergeAsc(xst, ys)
        else
          y :: mergeAsc(xs, yst)
    }

    def mergeTrees(ts: BinomialTrees[A])(implicit ord: Ordering[A]): BinomialTrees[A] = ts match {
      case x :: y :: rs if x.order < y.order => x :: mergeTrees(y :: rs)
      case x :: y :: rs if x.order > y.order => mergeTrees(y :: x :: rs)
      case x :: y :: rs => mergeTrees((x merge y) :: rs)
      case os => os
    }

  }

  class BinomialHeapIterator[A](var current: BinomialHeap[A]) extends Iterator[A] {

    override def hasNext: Boolean = current.nonEmpty

    override def next(): A = {
      val (value, nx) = current.remove
      current = nx
      value
    }

  }

}

case class BinomialTree[A] private(value: A, children: List[BinomialTree[A]]) {

  lazy val order: Int = children.size

  def leftmost: BinomialTree[A] = children.head

  def /::(lhs: BinomialTree[A]): BinomialTree[A] = {
    require(lhs.order == order)
    BinomialTree(value, lhs :: children)
  }

  def merge(rhs: BinomialTree[A])(implicit ord: Ordering[A]): BinomialTree[A] =
    if (ord.lteq(value, rhs.value)) rhs /:: this
    else this /:: rhs

  def split: Option[(BinomialTree[A], BinomialTree[A])] =
    if (children.isEmpty) None
    else Some(leftmost, BinomialTree(value, children.tail))

  /**
    * 2 ^order^
    */
  def size: Int = 1 << order

  def show: String = showLines mkString "\n"

  def showLines: Seq[String] =
    s"$value ($order)" +: (children.view.reverse flatMap (_.showLines) map ("  " + _)).force

}

object BinomialTree {

  def zero[A](value: A) = BinomialTree(value, Nil)

  def repeat[A](value: A): Stream[BinomialTree[A]] = {
    lazy val trees: Stream[BinomialTree[A]] =
      (BinomialTree zero value) #:: (trees map (t => t /:: t))
    trees
  }

}
