package learning.airt.concurrency.chapter4

import org.scalatest._

class SpawnSpec extends AsyncFreeSpec with Matchers {

  "Exercises in Chapter 4" - {

    "Spawn" - {
      "should work correctly" in {
        Spawn spawn "ls -lh /" map { exitCode =>
          exitCode shouldBe 0
        }
      }
    }

  }

}
