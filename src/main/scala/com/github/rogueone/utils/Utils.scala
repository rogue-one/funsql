package com.github.rogueone.utils

import java.util.UUID

object Utils {

  implicit class CaseInsensitiveRegex(sc: StringContext) {
    def ci = ( "(?i)" + sc.parts.mkString ).r
  }

  def uuid: String = UUID.randomUUID.toString

  implicit class StringUtil(str: String) {
    def oneLiner: String = str.split(System.lineSeparator).mkString(" ")
  }


}
