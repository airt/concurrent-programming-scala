package learning.airt.concurrency.chapter1

object Exercises {

  def compose[A, B, C](g: B => C, f: A => B): A => C = g compose f

  def fuse[A, B](xm: Option[A], ym: Option[B]): Option[(A, B)] =
    for {
      x <- xm
      y <- ym
    } yield (x, y)

  def check[T](xs: Seq[T])(p: T => Boolean): Boolean = xs forall p

  case class Pair[A, B](first: A, second: B)

  def permutations(s: String): Seq[String] =
    if (s.length == 0) Seq("")
    else for {
      i <- s.map(s indexOf _).distinct
      p <- permutations((s take i) + (s drop (i + 1)))
    } yield s(i) +: p

}
