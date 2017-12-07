package learning.concurrent.programming.chapter3

import scala.collection.generic._
import scala.collection.mutable

class SyncConcurrentMap[A, B]
    extends mutable.HashMap[A, B]
    with collection.concurrent.Map[A, B]
    with mutable.MapLike[A, B, SyncConcurrentMap[A, B]] {

  override def putIfAbsent(k: A, v: B): Option[B] = synchronized {
    get(k) match {
      case vo @ Some(_) => vo
      case None         => put(k, v)
    }
  }

  override def remove(k: A, ov: B): Boolean = synchronized {
    get(k) match {
      case Some(v) if v == ov => remove(k); true
      case _                  => false
    }
  }

  override def replace(k: A, v: B): Option[B] = synchronized {
    get(k) match {
      case vo @ Some(_) => put(k, v); vo
      case None         => None
    }
  }

  override def replace(k: A, ov: B, nv: B): Boolean = synchronized {
    get(k) match {
      case Some(v) if v == ov => put(k, nv); true
      case _                  => false
    }
  }

  override def empty: SyncConcurrentMap[A, B] = SyncConcurrentMap.empty[A, B]

}

object SyncConcurrentMap extends MutableMapFactory[SyncConcurrentMap] {

  override def empty[A, B]: SyncConcurrentMap[A, B] = new SyncConcurrentMap[A, B]

}
