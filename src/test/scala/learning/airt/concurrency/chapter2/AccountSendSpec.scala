package learning.airt.concurrency.chapter2

import org.scalatest._

class AccountSendSpec extends FreeSpec with Matchers with Inspectors {

  import AccountSend._

  "Exercises in Chapter 2" - {

    "send" - {
      "should work correctly" in {
        val a = Account("A", 100)
        val b = Account("B", 100)
        (1 to 100) foreach { _ =>
          thread {
            send(a, b, 10)
          }
          thread {
            send(b, a, 10)
          }
        }
        forAll(Seq(a, b))(_.money shouldBe 100)
      }
    }

    "sendAll" - {
      "should work correctly" in {
        val senders = Set(Account("A", 100), Account("B", 200), Account("C", 300))
        val target = Account("Z", 1000)
        sendAll(senders, target)
        forAll(senders)(_.money shouldBe 0)
        target.money shouldBe 1600
      }
    }

  }

}
