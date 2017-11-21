package com.whiker

/**
  * Created by whiker on 2017/1/2.
  */
class FooConstruct {
  println("FooConstruct主构造器被调用")

  def this(name: String) {
    this() // 调用主构造器
    println("FooConstruct第1个辅助构造器被调用, name: " + name)
  }

  def this(name: String, age: Int) {
    this()
    println("FooConstruct第2个辅助构造器被调用, name: " + name + ", age: " + age)
  }
}
