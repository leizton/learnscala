package com.whiker.actor

import akka.actor.{Actor, ActorRef, ActorSystem, Props, Terminated}

import scala.concurrent.Future

/**
  * Created by whiker on 2017/1/15.
  */
class ActorTest {

  class AsciiConverter extends Actor {
    // receive是Actor的抽象方法，返回一个偏函数
    def receive = {
      case ch: Char =>
        printf("receive ch: %c\n", ch)
        sender ! (ch, ch.toInt)
      case i: Int =>
        printf("receive i: %d\n", i)
        self ! (i.toChar, i)
      case s: String =>
        val reply = for (ch <- s) yield (ch, ch.toInt)
        sender ! reply
      case (ch: Char, i: Int) =>
        printf("receive (%c, %d)\n", ch, i)
      case isStop: Boolean =>
        if (isStop) context stop self
      case _ =>
        println("AsciiConverter unknown msg")
    }

    override def postStop() = println("ascii-actor stop")
  }

  /**
    * 推荐让伴生对象提供一个工厂方法来创建Props配置对象
    * 好处是避免暴露一个actor的引用(new AsciiConverter)
    */
  object AsciiConverter {
    def props: Props = Props(new AsciiConverter)
  }

  // 在actor系统内创建一个actor，并返回其引用
  private val actSys = ActorSystem("actor-test")
  private val actor: ActorRef = actSys.actorOf(AsciiConverter.props, name = "ascii-actor")

  def testTell() {
    // 向actor发消息，非阻塞
    actor.tell('a', actor)
    actor ! 97
    Thread sleep 1000
  }

  def testAsk() {
    import akka.pattern.ask

    import scala.concurrent.duration._

    val future: Future[Any] = actor.ask("abc")(1 seconds)
    Thread sleep 1000
    println(future.isCompleted)
    println(future.value)
  }

  def stop(isStop: Boolean) {
    actor ! isStop
    val future: Future[Terminated] = actSys.terminate()
    import scala.concurrent.ExecutionContext.Implicits.global
    future.foreach(_ => println("actor system stop"))
  }
}
