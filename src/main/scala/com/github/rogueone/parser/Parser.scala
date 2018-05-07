package com.github.rogueone.parser

import com.github.rogueone.ast
import com.github.rogueone.ast.Nodes
import com.github.rogueone.parser.Primitives.alphabet
import fastparse.all._
import fastparse.{WhitespaceApi, all}

object Parser {

  val White: WhitespaceApi.Wrapper = WhitespaceApi.Wrapper{NoTrace(Primitives.whitespace.rep)}

  def function: P[Nodes.Function] = {
    import fastparse.noApi._
    P (Primitives.identifier ~ !Primitives.whitespace ~ "(" ~/ expression.rep(sep=",") ~ ")") map {
      case (x, y: Seq[ast.Nodes.Exp]) => ast.Nodes.Function(x, y)
    }
  }

  def expression: fastparse.all.P[ast.Nodes.Exp] = MathParser.mathExp

}
