package services

class MinEditDistance {

  def compute (x: String, y: String): Int = {
    val a = Array("#") ++ x.split("")
    val b = Array("#") ++ y.split("")
    val t = Array.ofDim[Int](a.length, b.length)
    for (i <- 0 until a.length) { t(i)(0) = i }
    for (i <- 0 until b.length) { t(0)(i) = i }
    minEditDistance(a, b, t, 1, 1)
  }

  def minEditDistance (a: Array[String], b: Array[String], t: Array[Array[Int]], x: Int, y: Int): Int = {
    val nextX = if (y == b.length - 1) x + 1
                else x
    val nextY = if (y == b.length - 1) 1
                else y + 1
    if (x == a.length) {
      t(a.length - 1)(b.length - 1)
    } else {
      if (a(x) == b(y)) {
        t(x)(y) = t(x - 1)(y - 1)
      } else {
        t(x)(y) = List(t(x - 1)(y), t(x - 1)(y - 1), t(x)(y - 1)).min + 1
      }
      minEditDistance(a, b, t, nextX, nextY)
    }
  }
}
