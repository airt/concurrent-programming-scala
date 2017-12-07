package learning.concurrent.programming.chapter2

object AccountSend {

  def send(sender: Account, target: Account, amount: Int) {

    def adjust() {
      sender.money -= amount
      target.money += amount
    }

    val Seq(first, second) = Seq(sender, target) sortBy (_.id)

    first synchronized {
      second synchronized {
        adjust()
      }
    }
  }

  def sendAll(senders: Set[Account], target: Account) {

    def adjust() {
      target.money += (0 /: senders) { (total, sender) =>
        val money = sender.money
        sender.money = 0
        total + money
      }
    }

    def synchronizedAll[R](task: () => R): List[AnyRef] => R = {
      case x :: xs => x synchronized synchronizedAll(task)(xs)
      case _       => task()
    }

    synchronizedAll(() => adjust())((target :: senders.toList) sortBy (_.id))
  }

}

object SynchronizedProtectedUid {

  private var uid = 0L

  def next(): Long = synchronized {
    uid += 1
    uid
  }

}

case class Account(id: Long, name: String, var money: Int)

object Account {

  def apply(name: String, money: Int): Account =
    apply(SynchronizedProtectedUid next (), name, money)

}
