package learning.concurrent.programming.chapter4

import java.util.concurrent.ConcurrentHashMap

import scala.concurrent._

class IMap[A, B] {

  import scala.collection.JavaConverters._

  private val store = new ConcurrentHashMap[A, Promise[B]].asScala

  def update(k: A, v: B) {
    store putIfAbsent (k, Promise successful v) foreach (_ success v)
  }

  def apply(k: A): Future[B] = {
    val promise = {
      val newPromise = Promise[B]
      store putIfAbsent (k, newPromise) getOrElse newPromise
    }
    promise.future
  }

}

object IMap {

  def apply[A, B](): IMap[A, B] = new IMap[A, B]

}
