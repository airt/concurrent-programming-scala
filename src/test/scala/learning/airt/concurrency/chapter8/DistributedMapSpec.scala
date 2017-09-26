package learning.airt.concurrency.chapter8

import akka.actor._
import akka.testkit._
import org.scalatest._

import scala.async.Async._

class DistributedMapSpec
    extends TestKit(ActorSystem())
    with ImplicitSender
    with AsyncFreeSpecLike
    with Matchers
    with BeforeAndAfterAll {

  override def afterAll(): Unit = shutdown(system)

  private def bits(x: String) = Integer parseInt (x, 2)

  "Exercises in Chapter 8" - {

    "DistributedMap" - {
      "should send message correctly" in async {
        import DistributedMap._
        val probes = for (i <- 0 to 7) yield TestProbe(s"probe-shard-$i")
        val m = DistributedMap[Int, Int](probes map (_.ref))
        val resultOfGet1 = m get bits("0010000000000000000000000000001")
        probes(1) expectMsg Get(bits("0010000000000000000000000000001"))
        probes(1) reply None
        await(resultOfGet1)
        val resultOfGet7 = m get bits("1110000000000000000000000000010")
        probes(7) expectMsg Get(bits("1110000000000000000000000000010"))
        probes(7) reply None
        await(resultOfGet7)
        val resultOfUpdate1 = m update (bits("0010000000000000000000000000001"), 1)
        probes(1) expectMsg Update(bits("0010000000000000000000000000001"), 1)
        // noinspection ScalaUnnecessaryParentheses
        probes(1) reply (())
        await(resultOfUpdate1)
        val resultOfUpdate7 = m update (bits("1110000000000000000000000000010"), 2)
        probes(7) expectMsg Update(bits("1110000000000000000000000000010"), 2)
        // noinspection ScalaUnnecessaryParentheses
        probes(7) reply (())
        await(resultOfUpdate7)
        succeed
      }
      "should get and update correctly" in async {
        val m = DistributedMap[Int, Int](8)
        await(m get bits("0010000000000000000000000000001")) shouldBe None
        await(m update (bits("0010000000000000000000000000001"), 1))
        await(m get bits("0010000000000000000000000000001")) shouldBe Some(1)
      }
    }

  }

}
