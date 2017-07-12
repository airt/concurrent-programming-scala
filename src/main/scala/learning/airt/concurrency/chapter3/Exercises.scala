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

object Exercises extends LazyLogging {

  class PiggybackContext extends ExecutionContext {
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

  class TreiberStack[T] {
    private val vsr = new AtomicReference[List[T]](Nil)

    def push(v: T) {
      val ovs = vsr.get()
      val nvs = v :: ovs
      if (!vsr.compareAndSet(ovs, nvs)) push(v)
    }

    def pop(): T = {
      val ovs@(v :: nvs) = vsr.get()
      if (!vsr.compareAndSet(ovs, nvs)) pop()
      else v
    }
  }

  class LazyCell[T](initialization: => T) {
    @volatile private[this] var initialized = false
    private[this] var value: T = _

    def apply(): T = {
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
    def apply[T](initialization: => T): LazyCell[T] = new LazyCell(initialization)
  }

  class PureLazyCell[T](initialization: => T) {
    private[this] val vor = new AtomicReference[Option[T]](None)

    def apply(): T = apply(initialization)

    @tailrec
    private def apply(vc: => T): T = vor.get() match {
      case Some(v) => v
      case None =>
        val v = vc
        if (!vor.compareAndSet(None, Some(v))) apply(v)
        else v
    }
  }

  object PureLazyCell {
    def apply[T](initialization: => T): PureLazyCell[T] = new PureLazyCell(initialization)
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

  def spawn[T](block: => T): Try[T] = {
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
      val sources = Seq("target/scala-2.12/classes", "target/scala-2.12/test-classes")
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
        map(_.readObject().asInstanceOf[Try[T]]).
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
