package com.whiker

/**
  * Created by whiker on 2017/1/2.
  */
class FooDerive(name: String, age: Int, gender: String) extends FooConstruct(name, age) {
  println("FooDerive主构造器被调用, gender: " + gender)
}
