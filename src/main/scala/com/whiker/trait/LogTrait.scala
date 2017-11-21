package com.whiker.`trait`

import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

/**
  * Created by whiker on 2017/1/5.
  */
trait LogTrait {
  def log(fmt: String, args: Any*) {}
}

/**
  * 输出到控制台
  */
trait ConsoleLogTrait extends LogTrait {
  override def log(fmt: String, args: Any*) {
    printf(fmt + "\n", args: _*)
  }
}

/**
  * 添加时间戳
  */
trait TimestampLogTrait extends LogTrait {
  override def log(fmt: String, args: Any*) {
    super.log("[" + TimestampLogTrait.timeFmt.print(System.currentTimeMillis) + "] " + fmt, args: _*)
  }
}

object TimestampLogTrait {
  val timeFmt: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
}
