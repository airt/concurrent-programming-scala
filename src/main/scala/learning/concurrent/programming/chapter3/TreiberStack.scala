package learning.concurrent.programming.chapter3

import java.util.concurrent.atomic.AtomicReference

class TreiberStack[A] {

  private val vsr = new AtomicReference[List[A]](Nil)

  def push(v: A) {
    val ovs = vsr get ()
    val nvs = v :: ovs
    if (!(vsr compareAndSet (ovs, nvs))) push(v)
  }

  def pop(): A = {
    val ovs @ (v :: nvs) = vsr get ()
    if (!(vsr compareAndSet (ovs, nvs))) pop()
    else v
  }

}
