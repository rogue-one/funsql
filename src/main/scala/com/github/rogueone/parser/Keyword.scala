package com.github.rogueone.parser

import fastparse.all._
import fastparse.all.{IgnoreCase, P}


sealed abstract class Keyword(val word: String) {

  /**
    * creates a parser for a given keyword.
    * ensures keyword is not a subset of a larger literal value by negative lookahead (!).
    * makes keyword case in-sensitive.
    * @return
    */
  def parser: P[Unit] = P{IgnoreCase(word) ~ !(Primitives.alphabet | Primitives.number | Primitives.underscore)}

}

object Keyword {

  object Not extends Keyword("not")

  object Select extends Keyword("select")

  object From extends Keyword("from")

  object Where extends Keyword("where")

  object Or extends Keyword("or")

  object And extends Keyword("and")

  object Limit extends Keyword("limit")

  object Group extends Keyword("group")

  object By extends Keyword("by")

  object In extends Keyword("in")

  object As extends Keyword("as")

  object Join extends Keyword("join")

  object On extends Keyword("on")

  object Inner extends Keyword("inner")

  object Left extends Keyword("left")

  object Right extends Keyword("right")

  object Full extends Keyword("full")

  object Cross extends Keyword("cross")

  object Outer extends Keyword("outer")

  val keywords = Seq(Not, Select, From, Where, Or, And, Limit, Group, By, In, As, Join, On, Inner, Left, Right, Full,
    Cross, Outer)


}
