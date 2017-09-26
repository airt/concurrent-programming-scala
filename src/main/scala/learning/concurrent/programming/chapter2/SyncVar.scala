package learning.concurrent.programming.chapter2

class SyncVar[A] {

  private var variable: Option[A] = None

  def isEmpty: Boolean = synchronized(variable.isEmpty)

  def nonEmpty: Boolean = synchronized(variable.nonEmpty)

  def get(): A = synchronized {
    variable match {
      case Some(v) => variable = None; v
      case None => throw new NoSuchElementException("get from empty variable")
    }
  }

  def put(v: A): this.type = synchronized {
    variable match {
      case Some(_) => throw new IllegalStateException("put to nonempty variable")
      case None => variable = Some(v); this
    }
  }

  // noinspection AccessorLikeMethodIsEmptyParen
  def getWait(): A = synchronized {
    while (variable.isEmpty) wait()
    val Some(v) = variable
    variable = None
    notify()
    v
  }

  def putWait(v: A): this.type = synchronized {
    while (variable.nonEmpty) wait()
    variable = Some(v)
    notify()
    this
  }

}
