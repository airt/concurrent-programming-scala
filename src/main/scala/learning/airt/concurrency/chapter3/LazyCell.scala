package learning.airt.concurrency.chapter3

class LazyCell[A](initialization: => A) {

  @volatile private[this] var initialized = false
  private[this] var value: A = _

  def apply(): A = {
    if (!initialized) {
      this synchronized {
        if (!initialized) {
          value = initialization
          initialized = true
        }
      }
    }
    value
  }

}

object LazyCell {

  def apply[A](initialization: => A): LazyCell[A] = new LazyCell(initialization)

}
