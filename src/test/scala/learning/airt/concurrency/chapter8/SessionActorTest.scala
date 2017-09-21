package learning.airt.concurrency.chapter8

import akka.actor._
import akka.pattern._
import akka.testkit._
import org.scalatest._

import scala.async.Async._
import scala.concurrent.duration._

class SessionActorTest extends TestKit(ActorSystem()) with AsyncFreeSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll(): Unit = shutdown(system)

  "Exercises in Chapter 8" - {

    "SessionActor" - {
      "should work correctly" in async {
        import SessionActor._
        val probe = TestProbe()
        implicit val sender: ActorRef = probe.ref
        val session = system actorOf (SessionActor.props("secret", probe.ref), "session")
        session ! 0
        probe expectNoMsg ()
        session ! StartSession("secret")
        session ! 1
        probe expectMsg 1
        session ! StopSession
        session ! 2
        probe expectNoMsg ()
        session ! StartSession("incorrect")
        session ! 3
        probe expectNoMsg ()
        await(gracefulStop(session, 100.millis))
        succeed
      }
    }

  }

}
