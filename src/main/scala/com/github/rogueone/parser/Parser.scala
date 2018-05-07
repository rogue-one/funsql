package com.github.rogueone.parser

import com.github.rogueone.ast
import fastparse.all._
import fastparse.WhitespaceApi

object Parser {

  val White: WhitespaceApi.Wrapper = WhitespaceApi.Wrapper{NoTrace(Primitives.whitespace.rep)}

  def expression: fastparse.all.P[ast.Nodes.Exp] = MathParser.mathExp

}
