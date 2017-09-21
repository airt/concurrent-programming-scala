package learning.airt.concurrency.chapter8

import akka.actor._
import akka.testkit._
import org.scalatest._

class ExecutionContextActorTest extends TestKit(ActorSystem()) with FreeSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll(): Unit = shutdown(system)

  "Exercises in Chapter 8" - {

    "TimerActor" - {
      "should work correctly" in {
        import ExecutionContextActor._
        val probe = TestProbe()
        implicit val sender: ActorRef = probe.ref
        val executor = system actorOf (ExecutionContextActor props (), "executor")
        executor ! Execute { () =>
          // noinspection ScalaUselessExpression
          1 / 0
          probe.ref ! 1
        }
        probe expectNoMsg ()
        executor ! Execute { () =>
          probe.ref ! 2
        }
        probe expectMsg 2
      }
    }

  }

}
