package com.github.rogueone.utils

import java.util.UUID

object Utils {

  implicit class CaseInsensitiveRegex(sc: StringContext) {
    def ci = ( "(?i)" + sc.parts.mkString ).r
  }

  def uuid: String = UUID.randomUUID.toString

}
