package learning.airt.concurrency.chapter3

import org.scalatest._

import scala.util._

class SpawnSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 3" - {

    "spawn" - {
      "should work correctly" in {
        val x = Random nextInt ()
        val y = Random nextInt ()
        val result = Spawn spawn {
          x + y
        }
        result shouldBe Success(x + y)
      }
    }

  }

}
