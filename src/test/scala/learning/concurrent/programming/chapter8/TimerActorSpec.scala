package learning.concurrent.programming.chapter8

import akka.actor._
import akka.pattern._
import akka.testkit._
import learning.concurrent.programming.chapter5.Timer.timed
import org.scalatest._

import scala.async.Async._
import scala.concurrent.duration._

class TimerActorSpec
    extends TestKit(ActorSystem())
    with ImplicitSender
    with AsyncFreeSpecLike
    with Matchers
    with BeforeAndAfterAll {

  override def afterAll(): Unit = shutdown(system)

  "Exercises in Chapter 8" - {

    "TimerActor" - {
      "should work correctly" in async {
        import TimerActor._
        val timer = system actorOf (TimerActor.props, "timer")
        timer ! Register(100.millis)
        val time = timed(expectMsg(Timeout)) / 1000000
        time.toInt should be >= 100
        await(gracefulStop(timer, 100.millis))
        succeed
      }
    }

  }

}
