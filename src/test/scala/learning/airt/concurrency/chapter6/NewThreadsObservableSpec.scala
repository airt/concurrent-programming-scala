package learning.airt.concurrency.chapter6

import java.util.concurrent.ConcurrentHashMap

import org.scalatest._
import rx.lang.scala.Observable

import scala.collection.JavaConverters._
import scala.concurrent.duration._

class NewThreadsObservableSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 6" - {

    "NewThreadsObservable" - {
      "should work correctly" in {
        def thread(name: String): Thread = {
          val t = new Thread(name) {
            override def run(): Unit = Thread sleep 120
          }
          t.start()
          t
        }

        val rs = new ConcurrentHashMap[String, Thread].asScala
        val ob = Observable interval 100.millis flatMap { _ => NewThreadsObservable() }
        ob subscribe (t => rs.putIfAbsent(t.getName, t))
        Thread sleep 500
        (1 to 10) map { i => thread(s"test-thread-$i") } foreach (_ join())
        rs.size should be >= 10
      }
    }

  }

}
