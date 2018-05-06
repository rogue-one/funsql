package com.github.rogueone.utils

import com.github.rogueone.TestSpec
import Utils._

class UtilSpec extends TestSpec {

  "StringUtil" must "pattern match in case sensitive" in {
    "Bingo" match {
      case ci"bingo" => ()
      case ci"BiNGO" => ()
      case _ => fail("pattern match failed")
    }
  }
}
