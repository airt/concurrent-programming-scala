package learning.airt.concurrency.chapter2

import com.typesafe.scalalogging.LazyLogging

object ProducerAndConsumer extends LazyLogging {

  def run() {
    val sv = new SyncVar[Int]

    val consumer = thread {
      var v = 0
      while (v != 15) {
        if (sv.nonEmpty) {
          v = sv.get()
        } else Thread.`yield`()
      }
      logger.debug(s"consumer task completed")
    }

    val producer = thread {
      var v = 0
      while (v <= 15) {
        if (sv.isEmpty) {
          sv.put(v)
          v += 1
        } else Thread.`yield`()
      }
      logger.debug(s"producer task completed")
    }

    producer.join()
    consumer.join()
  }

  def runWithWait() {
    val sv = new SyncVar[Int]

    val consumer = thread {
      var v = 0
      while (v != 15) {
        v = sv.getWait()
      }
      logger.debug(s"consumer task with wait completed")
    }

    val producer = thread {
      var v = 0
      while (v <= 15) {
        sv.putWait(v)
        v += 1
      }
      logger.debug(s"producer task with wait completed")
    }

    producer.join()
    consumer.join()
  }

  def runWithQueue() {
    val sq = new SyncQueue[Int](5)

    val consumer = thread {
      var v = 0
      while (v != 15) {
        v = sq.getWait()
      }
      logger.debug(s"consumer task with queue completed")
    }

    val producer = thread {
      var v = 0
      while (v <= 15) {
        sq.putWait(v)
        v += 1
      }
      logger.debug(s"producer task with queue completed")
    }

    producer.join()
    consumer.join()
  }

}
