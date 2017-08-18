package learning.airt.concurrency.chapter1

import org.scalatest._

class ExercisesSpec extends FreeSpec with Matchers {

  import Exercises._

  "Exercises in Chapter 1" - {

    "compose" - {
      "should compose functions correctly" in {
        val f: Int => Int = _ + 1
        val g: Int => Int = _ * 2
        val c = compose(g, f)
        c(2) shouldBe 6
      }
    }

    "fuse" - {
      "should return a `some` when input both `some`" in {
        fuse(Some(1), Some(2)) shouldBe Some(1, 2)
      }
      "should return a `none` when input any `none`" in {
        fuse(Some(1), None) shouldBe None
      }
    }

    "check" - {
      "should return true when `p` always return true" in {
        check(1 to 9)(_ > 0) shouldBe true
      }
      "should return false when `p` sometimes return false" in {
        check(1 to 9)(_ > 1) shouldBe false
      }
    }

    "Pair" - {
      "can be used in pattern matching" in {
        Pair(1, 2) match {
          case pair @ Pair(first, second) =>
            pair shouldBe Pair(1, 2)
            first shouldBe 1
            second shouldBe 2
          case _ =>
            fail
        }
      }
    }

    "permutations" - {
      "should generate permutations" in {
        permutations("abb") should (have length 3 and contain allOf ("abb", "bab", "bba"))
      }
    }

  }

}
