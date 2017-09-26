package learning.concurrent.programming.chapter8

import akka.actor._
import akka.pattern._
import akka.testkit._
import org.scalatest._

import scala.async.Async._
import scala.concurrent.duration._

class ExecutionContextActorSpec
    extends TestKit(ActorSystem())
    with ImplicitSender
    with AsyncFreeSpecLike
    with Matchers
    with BeforeAndAfterAll {

  override def afterAll(): Unit = shutdown(system)

  "Exercises in Chapter 8" - {

    "TimerActor" - {
      "should work correctly" in async {
        import ExecutionContextActor._
        val executor = system actorOf (ExecutionContextActor props (), "executor")
        executor ! Execute { () =>
          // noinspection ScalaUselessExpression
          1 / 0
          testActor ! 1
        }
        expectNoMsg()
        executor ! Execute { () =>
          testActor ! 2
        }
        expectMsg(2)
        await(gracefulStop(executor, 100.millis))
        succeed
      }
    }

  }

}
