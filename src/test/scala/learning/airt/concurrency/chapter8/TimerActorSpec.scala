package learning.airt.concurrency.chapter8

import akka.actor._
import akka.testkit._
import learning.airt.concurrency.chapter5.Timer.timed
import org.scalatest._

import scala.concurrent.duration._

class TimerActorSpec extends TestKit(ActorSystem()) with FreeSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll(): Unit = shutdown(system)

  "Exercises in Chapter 8" - {

    "TimerActor" - {
      "should work correctly" in {
        import TimerActor._
        val probe = TestProbe()
        implicit val sender: ActorRef = probe.ref
        val timer = system actorOf (TimerActor.props, "timer")
        timer ! Register(100.millis)
        val time = timed { probe expectMsg Timeout } / 1000000
        time.toInt should be >= 100
      }
    }

  }

}
