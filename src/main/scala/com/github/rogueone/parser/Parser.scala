package com.github.rogueone.parser

import com.github.rogueone.ast
import com.github.rogueone.ast.Nodes
import com.github.rogueone.parser.Primitives.alphabet
import fastparse.all._
import fastparse.{WhitespaceApi, all}

object Parser {

  val expression: fastparse.all.P[ast.Nodes.Exp] = {
    function | MathParser.mathExp | identifier | LiteralParser.literal
  }

  val White: WhitespaceApi.Wrapper = WhitespaceApi.Wrapper{NoTrace(Primitives.whitespace.rep)}

  def identifier: P[Nodes.Identifier] = {
    P(((Primitives.alphabet | Primitives.underscore) ~ (alphabet | Primitives.number | Primitives.underscore).rep.?)
      .!.opaque("<identifier>"))
      .filter(!Keyword.keywords.map(_.word).contains(_)).map(Nodes.Identifier)
  }

  def function: P[Nodes.Function] = {
    import fastparse.noApi._
    P (Parser.identifier ~ !Primitives.whitespace ~ "(" ~/ expression.rep(sep=",") ~ ")") map {
      case (x, y: Seq[ast.Nodes.Exp]) => ast.Nodes.Function(x, y)
    }
  }
}
