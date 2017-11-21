package com.whiker

import com.google.common.base.Strings

/**
  * Created by whiker on 2017/1/6.
  */
class Name(familyName: String, nickName: String) {
  private val _familyName = familyName
  private val _nickName = nickName

  override def toString: String = {
    _familyName + Name.splitter + _nickName
  }
}

object Name {
  private val splitter = "."

  def apply(familyName: String, nickName: String): Name = new Name(familyName, nickName)

  def unapply(input: String): Option[(String, String)] = {
    val strs = Strings.nullToEmpty(input).trim.split("\\.")  // 需要转义
    if (strs.length != 2) {
      throw new IllegalArgumentException("Name.unapply, input:" + input)
    }
    Some(strs(0), strs(1))
  }
}
