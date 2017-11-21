package com.whiker

import java.lang.reflect.Method

import com.whiker.actor.ActorTest

import scala.collection.convert.Wrappers.JPropertiesWrapper
import scala.collection.mutable
import scala.compat.Platform.currentTime

/**
  * Created by whiker on 2016/12/31.
  */
object Main extends App {
  override def main(args: Array[String]) {
    val methods = collection.mutable.TreeMap[Int, Method]()
    classOf[Main].getDeclaredMethods.foreach(
      method => {
        val ano: WhCall = method.getDeclaredAnnotation(classOf[WhCall])
        if (ano != null) {
          methods += (ano.order() -> method)
        }
      }
    )

    lazy val main = new Main()
    for ((_, method) <- methods) {
      println(">> " + method.getName)
      method.invoke(main)
      println()
    }

    println("\nruntime: " + (currentTime - executionStart) + "ms")
  }
}

class Main {
  @WhCall(order = 1) def mutableMap() {
    var map = scala.collection.mutable.Map("a" -> 0)
    map("a") = 97
    map += ("A" -> 65)

    // 遍历map
    print("map: { ")
    for ((k, v) <- map) {
      print(k + ":" + v + " ")
    }
    println("}")

    // 遍历map
    print("map: { ")
    map.map { case (k, v) => print(k + ":" + v + " ") }  // 返回值: List((), ())
    println("}")

    map += ("#" -> 65)
    var map1 = map.map { case (k, v) => (v, k) }  // map1: Map(97 -> a, 65 -> #), map不变
  }

  @WhCall(order = 2) def exception() {
    try {
      println(1 / 0)
    } catch {
      case e: Throwable => println(e.getClass.getCanonicalName + ": " + e.getMessage)
    } finally {
    }
  }

  @WhCall(order = 3) def account() {
    val account = Account(100)
    account.income(10.71)
    account.pay(50.05)
    printf("结余￥%.2f\n", account.currentBalance)
  }

  @WhCall(order = 4) def enum() {
    val color = ColorEnum(1)
    if (color == ColorEnum.BLUE) {
      println("BLUE selected")
    }
  }

  @WhCall(order = 5) override def toString: String = {
    println("Main override toString")
    "Main"
  }

  @WhCall(order = 6) def instance() {
    val v = 1
    println(v.isInstanceOf[Int])
    val u = null
    println(u.isInstanceOf[Int])
  }

  @WhCall(order = 7) def fooConstruct() {
    new FooConstruct("foo1")
    new FooConstruct("foo2", 20)
  }

  @WhCall(order = 8) def fooDerive() {
    new FooDerive("foo2", 20, "boy")
  }

  @WhCall(order = 9) def anonymousDeriveClass() {
    val newMain = new Main() {
      override def toString = "annonymous Main"
    }
    println(newMain.toString)
  }

  @WhCall(order = 10) def testUnapply() {
    val name = Name("fan", "whiker")
    println(name)
    println(Name.unapply(name.toString))
  }

  // 函数变量
  @WhCall(order = 11) def funcVariable() {
    import scala.math._
    val func = sqrt _
    println(func(2))
    Array(1.0, 4, 9).map(func).foreach(e => print(e + " "))
    println

    val triple = 3 * (_: Int)
    val arr = Array(1, 2, 3).map(triple)
    println(arr.sameElements(Array(3, 6, 9)))
  }

  // 柯里化
  @WhCall(order = 12) def curry() {
    def until(condition: => Boolean)(block: => Unit): Unit = {
      if (!condition) {
        block
        until(condition)(block) // 尾递归
      }
    }

    var sum = 0
    var i = 10
    until(i == 0) {
      sum += i
      i -= 1
    }
    println(sum)
  }

  // 集合
  @WhCall(order = 13) def collect() {
    val set = mutable.TreeSet[Long](1, 3, 5) // apply
    println(set)
    set += 2
    println(set)
    set.add(4)
    println(set)
  }

  // 整数的10进制的每个数位
  @WhCall(order = 14) def testDigits() {
    def digits(n: Int): List[Int] = {
      if (n < 0) digits(-n)
      else if (n < 10) List(n)
      else digits(n / 10) :+ (n % 10) // seq :+ elem 或者 elem +: seq
    }

    println(digits(163452))
  }

  @WhCall(order = 15) def testList() {
    val lst = List(1, 2, 3)
    println(lst.head) // 第一个元素
    println(lst.tail) // 是一个List

    println(-1 :: 0 :: lst) // e3 :: (e2 :: (e1 :: lst或Nil))，右结合

    def sum(l: List[Int]): Int = if (l == Nil) 0 else l.head + sum(l.tail)

    println(sum(lst))
  }

  @WhCall(order = 15) def testMutableList() {
    val lst = mutable.LinkedList(1, -2, 3, -4)
    // 遍历lst，把负数变成正数
    var cur = lst
    while (cur.nonEmpty) {
      if (cur.elem < 0) cur.elem = -cur.elem
      cur = cur.next
    }
    println(lst)
    // 每2个元素删除1个
    cur = lst
    while (cur.nonEmpty && cur.next.nonEmpty) {
      cur.next = cur.next.next // 让cur新的下一个元素是cur.next.next
      cur = cur.next
    }
    println(lst)
  }

  // collect()，用于偏函数(不是对所有输入都定义输出的函数)
  @WhCall(order = 16) def testCollect() {
    val lst = "abcd".collect {
      case 'a' => 1
      case 'c' => 2
    }
    println(lst)
  }

  // reduceLeft() reduceRight() foldLeft()
  // 化简、折叠
  @WhCall(order = 17) def testReduce() {
    val lst = List(1, 2, 3)
    println(lst.reduceLeft(_ - _)) // ((1-2) - 3) 左结合
    println(lst.reduceRight(_ - _)) // (1 - (2-3)) 右结合
    println(lst.foldLeft(0)(_ - _)) // (((0-1) - 2) - 3) 设初值
  }

  // zip() zipAll() zipWithIndex
  @WhCall(order = 18) def testZip() {
    val chars = List('a', 'b', 'c')
    val ascii = List(97, 98, 99)

    // List(('a', 97), ('b', 98), ('c', 99))
    var lst = chars zip ascii
    println(lst)

    // lst1.zipAll( lst2, defaultValueForLst1, defaultValueForLst2 )
    // List(('a', 99), ('b', 0), ('c', 0))
    lst = chars.zipAll(ascii.takeRight(1), '0', 0)
    println(lst)

    // Vector(('a', 0), ('b', 1), ('c', 2))
    println("abc".zipWithIndex)
  }

  // 模式匹配
  @WhCall(order = 19) def testMatch() {
    val ch = '7'
    val digit = ch match {
      case '+' => 1
      case '-' => -1
      case _ if Character.isDigit(ch) => Character.digit(ch, 10)
      case _ => 0
    }
    println(digit)

    // 常量应以大写字母开头，或者用``包括起来
    import java.io.File._
    val str = ":" match {
      case `pathSeparator` => "match :"
      case _ => "no match :"
    }
    println(str)

    // 按类型匹配
    val obj: Any = BigInt(123)
    val num = obj match {
      case n: Int => n
      case s: String => Integer.parseInt(s)
      case _: BigInt => Integer.MAX_VALUE
      case _ => -1
    }
    println(num)

    // 数组和list的匹配
    val arr = Array(1, 2, 3)
    println(
      arr match {
        case Array(x) => x
        case Array(x, y) => x + y
        case Array(_, _, _*) => "many elems"
        case _ => "not an array"
      }
    )
    val lst = List(1, 2, 3)
    println(
      lst match {
        case _ :: Nil => "len 1"
        case _ :: _ :: Nil => "len 2"
        case _ :: _ :: tail => tail // List(3)
        case _ => "unknown"
      }
    )

    // 模式匹配的机制——提取器
    val pattern = "([0-9]+) ([a-z]+)".r
    "101 flower" match {
      case pattern(howMany, item) => printf("num: %s, item: %s\n", howMany, item)
      case _ => println("no match")
    }
    "Fan.whiker" match {
      // 调用Name::unapply(String):Option[(String, String)]
      // 把string拆开
      case Name(familyName, nickName) => printf("familyName: %s, nickName: %s\n", familyName, nickName)
      case _ => println("no match")
    }

    println("System Properties: ")
    for ((k, v) <- JPropertiesWrapper(System.getProperties)) {
      k match {
        case "user.dir" => printf("    (%s, %s)\n", k, v)
        case "user.home" => printf("    (%s, %s)\n", k, v)
        case "file.separator" => printf("    (%s, %s)\n", k, v)
        case _ =>
      }
    }

    // 偏函数
    val partFunc: PartialFunction[Char, Int] = {
      case '+' => 1
      case '-' => -1
      case _ => 0
    }
    println(partFunc('+'), partFunc('-'), partFunc('a'))
  }

  // 把递归优化成循环，或者直接写成尾递归
  @WhCall(order = 20) def tailCalls() {
    import scala.util.control.TailCalls._

    def sum(seq: Seq[Int], sumOfLast: Int): TailRec[Int] = {
      if (seq.isEmpty) done(sumOfLast)
      else tailcall[Int](sum(seq.tail, sumOfLast + seq.head))
    }

    val ret = sum(1 to 10, 0).result
    println(ret)
  }

  @WhCall(order = 21) def testActor(): Unit = {
    val test = new ActorTest
    test.testTell()
    test.testAsk()
    test.stop(true) // 调用stop(false)时，程序结束后AsciiActor::postStop()没有调用
  }

}
