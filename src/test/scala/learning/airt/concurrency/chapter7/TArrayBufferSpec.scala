package learning.airt.concurrency.chapter7

import org.scalatest._

class TArrayBufferSpec extends FreeSpec with Matchers {

  "Exercises in Chapter 7" - {

    "TArrayBuffer" - {

      "builder and iterator" - {
        "should work correctly" in {
          val buffer = TArrayBuffer(1 to 16: _*)
          buffer.toList shouldBe (1 to 16)
          a[NoSuchElementException] should be thrownBy {
            buffer.iterator drop 16 next()
          }
        }
      }

      "apply" - {
        "should work correctly" in {
          val buffer = TArrayBuffer(1 to 16: _*)
          buffer(1) shouldBe 2
          buffer(15) shouldBe 16
        }
        "should throw exception when exists out-of-bounds access" in {
          val buffer = TArrayBuffer(1 to 16: _*)
          an[IndexOutOfBoundsException] should be thrownBy {
            buffer(16)
          }
        }
      }

      "update" - {
        "should work correctly" in {
          val buffer = TArrayBuffer(1 to 16: _*)
          buffer(1) shouldBe 2
          buffer(1) = 20
          buffer(1) shouldBe 20
        }
      }

      "length" - {
        "should work correctly" in {
          val buffer = TArrayBuffer(1 to 16: _*)
          buffer.length shouldBe 16
        }
      }

      "+=" - {
        "should work correctly" in {
          val buffer = TArrayBuffer(1 to 16: _*)
          buffer += 20
          buffer.length shouldBe 17
          buffer(16) shouldBe 20
        }
      }

      "+=:" - {
        "should work correctly" in {
          val buffer = TArrayBuffer(1 to 16: _*)
          20 +=: buffer
          buffer.length shouldBe 17
          buffer.head shouldBe 20
          buffer(1) shouldBe 1
          buffer(16) shouldBe 16
        }
      }

      "insertAll" - {
        "should work correctly" in {
          val buffer = TArrayBuffer(1 to 16: _*)
          buffer insertAll(10, 21 to 37)
          buffer.length shouldBe 33
          buffer(9) shouldBe 10
          buffer(10) shouldBe 21
          buffer(26) shouldBe 37
          buffer(27) shouldBe 11
          buffer(32) shouldBe 16
        }
      }

      "remove" - {
        "should work correctly" in {
          val buffer = TArrayBuffer(1 to 16: _*)
          buffer remove 9
          buffer.length shouldBe 15
          buffer(8) shouldBe 9
          buffer(9) shouldBe 11
          buffer(14) shouldBe 16
        }
      }

      "clear" - {
        "should work correctly" in {
          val buffer = TArrayBuffer(1 to 16: _*)
          buffer clear()
          buffer.length shouldBe 0
        }
      }

      "toString" - {
        "should work correctly" in {
          val buffer = TArrayBuffer(1, 2, 3)
          buffer.toString shouldBe "TArrayBuffer(1, 2, 3)"
        }
      }

    }

  }

}
