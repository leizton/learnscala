package com.whiker

import com.whiker.`trait`.{ConsoleLogTrait, LogTrait, TimestampLogTrait}

/**
  * Created by whiker on 2016/12/31.
  */
class Account private(val id: Int, initBalance: Double) extends LogTrait {
  log("创建账户, id: %d", id)

  private var _balance = initBalance

  def income(v: Double) {
    if (v < 0) {
      throw new IllegalArgumentException("负收入")
    }
    _balance += v
    log("收入%.2f, 结余%.2f", v, _balance)
  }

  def pay(v: Double) {
    if (v > _balance) {
      throw new IllegalArgumentException("钱不够了")
    }
    _balance -= v
    log("支出%.2f, 结余%.2f", v, v, _balance)
  }

  def currentBalance: Double = _balance
}

object Account {
  var accountNum = 0

  private def newAccountId(): Int = {
    accountNum += 1
    accountNum
  }

  def apply(initBalance: Double): Account = new Account(
    newAccountId(), initBalance
  ) with ConsoleLogTrait with TimestampLogTrait
}
