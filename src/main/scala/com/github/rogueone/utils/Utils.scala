package com.github.rogueone.utils

object Utils {

  implicit class CaseInsensitiveRegex(sc: StringContext) {
    def ci = ( "(?i)" + sc.parts.mkString ).r
  }

}
