package learning.airt.concurrency.chapter3

import java.util.concurrent.atomic.AtomicReference

import com.typesafe.scalalogging.LazyLogging
import resource.managed

import scala.annotation.tailrec
import scala.collection.generic._
import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.util._
import scala.util.control.NonFatal

object Exercises {

  class PiggybackContext extends ExecutionContext with LazyLogging {
    override def execute(task: Runnable) {
      try {
        task.run()
      } catch {
        case NonFatal(e) => reportFailure(e)
      }
    }

    override def reportFailure(cause: Throwable) {
      logger.warn(s"piggyback task exception: ${cause.getMessage}")
    }
  }

  class TreiberStack[A] {
    private val vsr = new AtomicReference[List[A]](Nil)

    def push(v: A) {
      val ovs = vsr.get()
      val nvs = v :: ovs
      if (!vsr.compareAndSet(ovs, nvs)) push(v)
    }

    def pop(): A = {
      val ovs@(v :: nvs) = vsr.get()
      if (!vsr.compareAndSet(ovs, nvs)) pop()
      else v
    }
  }

  class ConcurrentSortedList[A](implicit val ord: Ordering[A]) {
    def add(v: A) {
      addTo(rr, v)
    }

    @tailrec
    private def addTo(vsr: ListAtomicRef[A], v: A) {
      vsr.get() match {
        case vs@CCons(h, t) =>
          if (ord.lteq(v, h)) {
            if (!vsr.compareAndSet(vs, CCons(v, newListAtomicRef(vs)))) addTo(vsr, v)
          } else {
            addTo(t, v)
          }
        case vs@CNil =>
          if (!vsr.compareAndSet(vs, CCons(v, newListAtomicRef(vs)))) addTo(vsr, v)
      }
    }

    def iterator: Iterator[A] = new Iterator[A] {
      private var current = rr.get()

      override def hasNext: Boolean = current != CNil

      override def next(): A = current match {
        case CCons(h, t) => current = t.get(); h
        case CNil => Iterator.empty.next()
      }
    }

    private val rr = newListAtomicRef(CNil)

    private type ListAtomicRef[E] = AtomicReference[CList[E]]

    private trait CList[+E]

    private case object CNil extends CList[Nothing]

    private case class CCons[E](h: E, t: ListAtomicRef[E]) extends CList[E]

    private def newListAtomicRef(vs: CList[A]) = new AtomicReference[CList[A]](vs)
  }

  class LazyCell[A](initialization: => A) {
    @volatile private[this] var initialized = false
    private[this] var value: A = _

    def apply(): A = {
      if (!initialized) {
        this synchronized {
          if (!initialized) {
            value = initialization
            initialized = true
          }
        }
      }
      value
    }
  }

  object LazyCell {
    def apply[A](initialization: => A): LazyCell[A] = new LazyCell(initialization)
  }

  class PureLazyCell[A](initialization: => A) {
    private[this] val vor = new AtomicReference[Option[A]](None)

    def apply(): A = apply(initialization)

    @tailrec
    private def apply(vc: => A): A = vor.get() match {
      case Some(v) => v
      case None =>
        val v = vc
        if (!vor.compareAndSet(None, Some(v))) apply(v)
        else v
    }
  }

  object PureLazyCell {
    def apply[A](initialization: => A): PureLazyCell[A] = new PureLazyCell(initialization)
  }

  class SyncConcurrentMap[A, B]
    extends mutable.HashMap[A, B]
      with collection.concurrent.Map[A, B]
      with mutable.Map[A, B]
      with mutable.MapLike[A, B, SyncConcurrentMap[A, B]] {

    override def putIfAbsent(k: A, v: B): Option[B] = synchronized {
      get(k) match {
        case vo@Some(_) => vo
        case None => put(k, v)
      }
    }

    override def remove(k: A, ov: B): Boolean = synchronized {
      get(k) match {
        case Some(v) if v == ov => remove(k); true
        case _ => false
      }
    }

    override def replace(k: A, v: B): Option[B] = synchronized {
      get(k) match {
        case vo@Some(_) => put(k, v); vo
        case None => None
      }
    }

    override def replace(k: A, ov: B, nv: B): Boolean = synchronized {
      get(k) match {
        case Some(v) if v == ov => put(k, nv); true
        case _ => false
      }
    }

    override def empty: SyncConcurrentMap[A, B] = SyncConcurrentMap.empty[A, B]
  }

  object SyncConcurrentMap extends MutableMapFactory[SyncConcurrentMap] {
    override def empty[A, B]: SyncConcurrentMap[A, B] = new SyncConcurrentMap[A, B]
  }

}

object Spawn extends LazyLogging {

  def spawn[A](block: => A): Try[A] = {
    import java.io._
    val actionFile = File.createTempFile(s"spawn-action-${System.currentTimeMillis()}", ".tmp")
    val resultFile = File.createTempFile(s"spawn-result-${System.currentTimeMillis()}", ".tmp")
    actionFile.deleteOnExit()
    resultFile.deleteOnExit()

    managed(new ObjectOutputStream(new FileOutputStream(actionFile))) foreach {
      _.writeObject(() => block)
    }

    import scala.sys.process._
    val executorClassName = SpawnExecutor.getClass.getName.stripSuffix("$")
    val classPaths = {
      val dependenciesClassPath = "target/streams/test/dependencyClasspath/$global/streams/export"
      val dependencies = io.Source.fromFile(dependenciesClassPath).getLines().toSeq
      val scv = util.Properties.versionNumberString.split("\\.").take(2).mkString(".")
      val sources = Seq(s"target/scala-$scv/classes", s"target/scala-$scv/test-classes")
      dependencies ++ sources
    }
    val command = Seq(
      "java", "-cp",
      classPaths.mkString(":"),
      executorClassName,
      actionFile.getCanonicalPath,
      resultFile.getCanonicalPath
    )

    logger.debug(s"command:\n${command.mkString(" ")}")

    Try(command.!!) flatMap { output =>
      logger.debug(s"spawn output: [\n$output\n]")
      managed(new ObjectInputStream(new FileInputStream(resultFile))).
        map(_.readObject().asInstanceOf[Try[A]]).
        tried.flatten
    }
  }

}

object SpawnExecutor {
  def main(args: Array[String]) {
    import java.io._
    val Array(actionInputPath, resultOutputPath) = args
    for {
      actionInput <- managed(new ObjectInputStream(new FileInputStream(actionInputPath)))
      resultOutput <- managed(new ObjectOutputStream(new FileOutputStream(resultOutputPath)))
    } {
      val action = actionInput.readObject().asInstanceOf[() => Any]
      val result = Try(action())
      resultOutput.writeObject(result)
    }
  }
}
