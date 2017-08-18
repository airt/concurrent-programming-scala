package learning.airt.concurrency.chapter6

import rx.lang.scala.Observable

class Signal[A] protected (val ob: Observable[A], var cv: Option[A] = None) {

  ob subscribe (v => cv = Option(v))

  def apply(): A = cv.get

  def map[B](f: A => B): Signal[B] =
    new Signal(ob map f, cv map f)

  def zip[B](rhs: Signal[B]): Signal[(A, B)] =
    new Signal(ob zip rhs.ob, for (vx <- cv; vy <- rhs.cv) yield (vx, vy))

  def scan[B](z: B)(f: (B, A) => B): Signal[B] =
    new Signal((ob scan z)(f), cv map (v => f(z, v)))

}

object Signal {

  def from[A](ob: Observable[A]): Signal[A] = new Signal(ob)

}
