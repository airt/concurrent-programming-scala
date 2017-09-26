package learning.airt.concurrency.chapter8

import akka.actor._
import akka.pattern._
import akka.util.Timeout

import scala.concurrent._
import scala.concurrent.duration._

class DistributedMap[A, B](shards: IndexedSeq[ActorRef]) {

  import DistributedMap._

  private val n = log2(shards.length)

  private implicit val timeout: Timeout = 5.seconds

  def get(key: A): Future[Option[B]] =
    (shards(indexByKey(key)) ? Get(key)).mapTo[Option[B]]

  def update(key: A, value: B): Future[Unit] =
    (shards(indexByKey(key)) ? Update(key, value)).mapTo[Unit]

  private def indexByKey(key: A) = key.## >> (31 - n)

}

object DistributedMap {

  def apply[A, B](nShards: Int = availableProcessors)(implicit system: ActorSystem): DistributedMap[A, B] =
    apply(for (i <- 0 until nShards) yield system actorOf (ShardActor.props[A, B], s"shard-$i"))

  def apply[A, B](shards: IndexedSeq[ActorRef]): DistributedMap[A, B] = new DistributedMap(shards)

  private def log2(x: Int): Int = ((math log10 x) / (math log10 2)).toInt

  case class Get[A](key: A)

  case class Update[A, B](key: A, value: B)

  class ShardActor[A, B] extends Actor {

    private var m = Map[A, B]()

    def receive: Receive = {
      // `ClassTag` can be used here
      case Get(key) =>
        sender() ! (m get key.asInstanceOf[A])
      case Update(key, value) =>
        sender() ! (m += (key -> value).asInstanceOf[(A, B)])
    }

  }

  object ShardActor {

    def props[A, B]: Props = Props[ShardActor[A, B]]

  }

}
