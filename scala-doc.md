scala的通配符是_
所以是import scala.math._ 而不是 import scala.math.*(java的通配符)

在C++中，对象的虚函数表的指针在超类构造方法执行时被设置成指向超类的虚函数表，父类构造完成后才指向子类的虚函数表。
所以对于C++，我们没有办法通过子类重写来改变父类构造方法的行为。
而Java设计者认为没必要，所以JVM在对象构造过程中不调整虚函数表，因此子类重写某些超类的方法可以改变超类的构造过程。

Any是所有类的超类，2个直接子类是AnyVal(Boolean Int Byte Unit等)、AnyRef(所有引用的超类)。

scala的Unit类型相当于c++的void，Unit类型只有一个取值"()"，即一个空的元组。
Nothing是继承了Any的Trait
Nil是List[Nothing]的实例

scala的trait(特质)相当于java的接口

1 to 10，实际是1.to(10)
a += b，实际是a = a.+(b)
1 -> 10，实际是1.->(10)，返回对偶(1,10)，对偶是只有2个元素的元组

scala可以重载+ - * /

f(arg0, ...) 当f不上函数时，实际是 f.apply(arg0, ...)
f(arg0, ...) = value，实际是 f.update(arg0, ..., value)
在类的伴生对象里定义 apply update unapply

闭包，是由语句块和语句块内的非局部变量构成的函数。比如Java的匿名类的对象。
闭包实际以类对象的方式实现。

柯里化，是把原先接受2个参数的函数转成新的只接受1个参数的函数的过程。
新函数的1个参数是原函数的第一个参数，新函数返回一个以原函数的第二个参数作参数的函数。
例子: def mul(x, y:Int) = x*y 柯里化 def mulCurry(x:Int) = (y:Int) = x*y
mul(2,3) 就是 mulCurry(2)(3)

集合的trait
Iterable <-- Seq <-- IndexedSeq
         <-- Set <-- SortedSet
         <-- Map <-- SortedMap

集合的apply方法，产生不可变集合
Set(1, 2, 3)
SortedSet(3, 2, 1)
Map(1->"a", 2->"b")

seq :+ elem 或者 elem +: seq

Scala的模式匹配和C++的switch的区别是，不会'意外地再次掉入另一个分支'，C++必须加break来避免
Scala的"match-case"当匹配失败时，抛出"scala.MatchError"异常

Scala用注解@throw代替Java的throw关键字
@throw(ClassOf[IOException]) def read(buf: Array[Byte], size: Int):Int
表明read()方法可能抛出受检异常IOException

语法(grammar)，是一组用于分析所有遵循某个特定结构的字符串的规则。
一组没有除法的算术表达式规则，可以定义成如下：
    。每个整数都看成算术表达式
    。+ - *是操作符
    。如果left right是算术表达式，且op是操作符，则left op right也是算术表达式
    。如果expr是算术表达式，则(expr)也是算术表达式
上述规则用巴科斯范式(BNF)表示如下：
    op ::= "+" | "-" | "*"
    expr ::= number | expr op expr | "("expr")"

  Golang CSP(communicating sequential processes) 进行顺序通信的进程
  CSP模型里message和channel是主体，处理器是匿名的。
发送方关心的是消息应发送到那个通道，而不是发给谁。发送方不关心消费者是谁，以及有多少个消费者。
  Actor模型里actor是主体，发送方关心谁会消费这个消息。所以建立的actor系统是树形分层系统，
父节点发消息给子节点，父节点知道自己有哪些子节点。

  Actor系统的精髓在于任务被拆开、委托，直到任务小到可以被完整地处理。这样做不仅清晰地划分出了任务本身的结构，
而且最终的actor也能按照它们“应该处理什么类型的消息”，“如何处理正常流程”以及“如何应对失败流程”来进行推理。
  如果一个actor对某种状况无法进行处理，它会发送相应的失败消息给它的监管者请求帮助。
  这样的递归结构使得失败能够在正确的层次得到处理。
  可以将这种思想与分层的设计方法进行比较。分层的设计方法最终很容易形成防护性编程，以防止任何失败被泄露出来；
相比之下把问题交由正确的人处理会是比将所有的事情“藏在深处”更好的解决方案。
  传统的分层是不递归的，上层不关心底层发生了什么，即使是错误。

Actor最佳实践：
    > actor以事件驱动方式来处理事件，他不应被任何一个外部实体所阻塞，如锁、io
    > 不要在actor之间传递可变对象
    > 不要在消息中放置行为(如闭包)，因为actor封装了行为和状态，消息中的闭包可能包含可变对象

sealed关键字
修饰class或trait，密封。不允许被extends，除非在同一个源文件中。即java中修饰类的final。
另外，sealed提示编译器检查模式匹配时，要检查所有case都被定义，没有遗漏。
例子：
    sealed trait State { def state: Byte }
    case object Running extends State { val state: Byte = 0 }
    case object Stop    extends State { val state: Byte = 1 }
    val state: State = Running
    state match {
        case Running => println("running")
        case Stop => println("stop")
    } // 输出"running"