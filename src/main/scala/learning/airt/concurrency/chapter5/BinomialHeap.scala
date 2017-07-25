package learning.airt.concurrency.chapter5

import learning.airt.concurrency.chapter5.BinomialTree.BinomialTrees

/**
  * @param trees ascending on value
  */
class BinomialHeap[A: Ordering] private(
  val trees: BinomialTrees[A] = Nil
) extends Iterable[A] {

  def insert(value: A): BinomialHeap[A] =
    this merge new BinomialHeap((BinomialTree zero value) :: Nil)

  def remove: (A, BinomialHeap[A]) = {
    val minTree = trees.minBy(_.value)
    val tmpHeap = new BinomialHeap(minTree.children.reverse)
    val newHeap = new BinomialHeap(this.trees filter (_ != minTree)) merge tmpHeap
    (minTree.value, newHeap)
  }

  def minimum: A = trees.map(_.value).min

  def merge(rhs: BinomialHeap[A]): BinomialHeap[A] =
    new BinomialHeap((mergeTrees compose mergeAscending) (trees, rhs.trees))

  private lazy val mergeAscending: ((BinomialTrees[A], BinomialTrees[A])) => BinomialTrees[A] = {
    case (Nil, ys) => ys
    case (xs, Nil) => xs
    case (xs@(x :: xst), ys@(y :: yst)) =>
      if (x.order <= y.order)
        x :: mergeAscending(xst, ys)
      else
        y :: mergeAscending(yst, xs)
  }

  private lazy val mergeTrees: BinomialTrees[A] => BinomialTrees[A] = {
    case x :: y :: rs if x.order < y.order => x :: mergeTrees(y :: rs)
    case x :: y :: rs if x.order > y.order => mergeTrees(y :: x :: rs)
    case x :: y :: rs => mergeTrees(mergeTree(x, y) :: rs)
    case os => os
  }

  private def mergeTree(x: BinomialTree[A], y: BinomialTree[A]): BinomialTree[A] =
    if (implicitly[Ordering[A]].lteq(x.value, y.value))
      y /:: x
    else
      x /:: y

  override def size: Int = (trees map (_.size)).sum

  override def isEmpty: Boolean = trees == Nil

  override def iterator: Iterator[A] = new BinomialHeapIterator(this)

  private class BinomialHeapIterator(var current: BinomialHeap[A]) extends Iterator[A] {
    override def hasNext: Boolean = current.nonEmpty

    override def next(): A = {
      val (value, nx) = current.remove
      current = nx
      value
    }
  }

  def show: String = showLines mkString "\n"

  private def showLines: Seq[String] =
    "BinomialHeap {" +: (trees map (_.showLines) reduce (_ ++ Seq(",") ++ _)) :+ "}"

}

object BinomialHeap {

  def apply[A: Ordering](xs: A*): BinomialHeap[A] = (empty[A] /: xs) (_ insert _)

  def empty[A: Ordering] = new BinomialHeap[A]

}

case class BinomialTree[A] private(value: A, order: Int, children: List[BinomialTree[A]]) {

  require(order == children.size)

  def strictSize: Int = 1 + children.map(_.strictSize).sum

  /**
    * 2 ^order^
    */
  def size: Int = 1 << order

  def leftmost: BinomialTree[A] = children.head

  def /::(lhs: BinomialTree[A]): BinomialTree[A] = {
    require(lhs.order == order)
    BinomialTree(value, 1 + order, lhs :: children)
  }

  override def toString: String = show

  def show: String = showLines mkString "\n"

  def showLines: Seq[String] =
    s"$value ($order)" +: (children.view.reverse flatMap (_.showLines) map ("  " + _)).force

}

object BinomialTree {

  type BinomialTrees[A] = List[BinomialTree[A]]

  def zero[A](value: A) = BinomialTree(value, 0, Nil)

  def repeat[A](value: A): Stream[BinomialTree[A]] = {
    lazy val trees: Stream[BinomialTree[A]] =
      (BinomialTree zero value) #:: (trees map (t => t /:: t))
    trees
  }

}
